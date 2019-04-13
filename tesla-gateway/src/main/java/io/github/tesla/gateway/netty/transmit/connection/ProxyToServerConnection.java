package io.github.tesla.gateway.netty.transmit.connection;

import static io.github.tesla.gateway.netty.transmit.ConnectionState.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.util.concurrent.RejectedExecutionException;

import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.HostAndPort;

import io.github.tesla.filter.AbstractPlugin;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.utils.ProxyUtils;
import io.github.tesla.gateway.netty.HttpFiltersAdapter;
import io.github.tesla.gateway.netty.HttpProxyServer;
import io.github.tesla.gateway.netty.transmit.ConnectionState;
import io.github.tesla.gateway.netty.transmit.flow.ConnectionFlow;
import io.github.tesla.gateway.netty.transmit.flow.ConnectionFlowStep;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;

/**
 * <p>
 * Represents a connection from our proxy to a server on the web. ProxyConnections are reused fairly liberally, and can
 * go from disconnected to connected, back to disconnected and so on.
 * </p>
 *
 * <p>
 * Connecting a {@link ProxyToServerConnection} can involve more than just connecting the underlying {@link Channel}. In
 * particular, the connection may use encryption (i.e. TLS) and it may also establish an HTTP CONNECT tunnel. The
 * various steps involved in fully establishing a connection are encapsulated in the property {@link #connectionFlow},
 * which is initialized in {@link #initializeConnectionFlow()}.
 * </p>
 */
@Sharable
public class ProxyToServerConnection extends ProxyConnection<HttpResponse> {
    private static final AttributeKey<Object> KEY_CONTEXT = AttributeKey.valueOf("SW_CONTEXT");
    private final ClientToProxyConnection clientConnection;
    private final ProxyToServerConnection serverConnection = this;
    private final String serverHostAndPort;
    private final boolean enableSSL;
    private final SslContext sslContext;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyToServerConnection.class);
    private String tag;

    /**
     * While we're in the process of connecting, it's possible that we'll receive a new message to write. This lock
     * helps us synchronize and wait for the connection to be established before writing the next message.
     */
    private final Object connectLock = new Object();

    private volatile InetSocketAddress remoteAddress;
    private volatile InetSocketAddress localAddress;
    /**
     * The filters to apply to response/chunks received from server.
     */
    private volatile HttpFiltersAdapter currentFilters;
    /**
     * Encapsulates the flow for establishing a connection, which can vary depending on how things are configured.
     */
    private volatile ConnectionFlow connectionFlow;
    /**
     * Disables SNI when initializing connection flow in {@link #initializeConnectionFlow()}. This value is set to true
     * when retrying a connection without SNI to work around Java's SNI handling issue (see
     * {@link #connectionFailed(Throwable)}).
     */
    private volatile boolean disableSni = false;
    /**
     * This is the initial request received prior to connecting. We keep track of it so that we can process it after
     * connection finishes.
     */
    private volatile HttpRequest initialRequest;
    /**
     * Keeps track of HttpRequests that have been issued so that we can associate them with responses that we get back
     */
    private volatile HttpRequest currentHttpRequest;

    /**
     * While we're doing a chunked transfer, this keeps track of the initial HttpResponse object for our transfer (which
     * is useful for its headers).
     */
    private volatile HttpResponse currentHttpResponse;
    /**
     * Limits bandwidth when throttling is enabled.
     */
    private volatile GlobalTrafficShapingHandler trafficHandler;

    public static ProxyToServerConnection create(HttpProxyServer proxyServer, ClientToProxyConnection clientConnection,
        String serverHostAndPort, HttpFiltersAdapter initialFilters, HttpRequest initialHttpRequest,
        GlobalTrafficShapingHandler globalTrafficShapingHandler) throws UnknownHostException {
        return new ProxyToServerConnection(proxyServer, clientConnection, serverHostAndPort, initialFilters,
            globalTrafficShapingHandler, initialHttpRequest);
    }

    private ProxyToServerConnection(HttpProxyServer proxyServer, ClientToProxyConnection clientConnection,
        String serverHostAndPort, HttpFiltersAdapter initialFilters,
        GlobalTrafficShapingHandler globalTrafficShapingHandler, HttpRequest initialHttpRequest)
        throws UnknownHostException {
        super(DISCONNECTED, proxyServer);
        this.clientConnection = clientConnection;
        this.serverHostAndPort = serverHostAndPort;
        this.trafficHandler = globalTrafficShapingHandler;
        this.currentFilters = initialFilters;
        currentFilters.proxyToServerConnectionQueued();
        this.enableSSL =
            YesOrNoEnum.YES.getCode().equals(initialHttpRequest.headers().get(PluginDefinition.X_TESLA_ENABLE_SSL));
        setupConnectionParameters();
        this.sslContext = setupSSLContext(initialHttpRequest);
    }

    private SslContext setupSSLContext(HttpRequest initialHttpRequest) {
        if (enableSSL) {
            try {
                SslContext sslCtx;
                String selfFileId = initialHttpRequest.headers().get(PluginDefinition.X_TESLA_SELF_SIGN_CRT);
                if (StringUtils.isNotBlank(selfFileId)) {
                    sslCtx = SslContextBuilder.forClient()
                        .trustManager(new ByteArrayInputStream(AbstractPlugin.getFileBytesByKey(selfFileId))).build();
                    initialHttpRequest.headers().remove(PluginDefinition.X_TESLA_SELF_SIGN_CRT);
                } else {
                    TrustManagerFactory instance =
                        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    KeyStore keyStore = null;
                    instance.init(keyStore);
                    sslCtx = SslContextBuilder.forClient().trustManager(instance).build();
                }
                initialHttpRequest.headers().remove(PluginDefinition.X_TESLA_ENABLE_SSL);
                return sslCtx;
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return null;
    }

    /***************************************************************************
     * Reading
     **************************************************************************/

    @Override
    public void read(Object msg) {
        if (isConnecting()) {
            LOG.debug("In the middle of connecting, forwarding message to connection flow: {}", msg);
            this.connectionFlow.read(msg);
        } else {
            super.read(msg);
        }
    }

    @Override
    public ConnectionState readHTTPInitial(HttpResponse httpResponse) {
        LOG.debug("Received raw response: {}", httpResponse);

        if (httpResponse.decoderResult().isFailure()) {
            LOG.debug("Could not parse response from server. Decoder result: {}",
                httpResponse.decoderResult().toString());
            FullHttpResponse substituteResponse = ProxyUtils.createFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.BAD_GATEWAY, "Unable to parse response from server");
            HttpUtil.setKeepAlive(substituteResponse, false);
            httpResponse = substituteResponse;
        }
        currentFilters.serverToProxyResponseReceiving();
        rememberCurrentResponse(httpResponse);
        respondWith(httpResponse);
        if (ProxyUtils.isChunked(httpResponse)) {
            return AWAITING_CHUNK;
        } else {
            currentFilters.serverToProxyResponseReceived();
            return AWAITING_INITIAL;
        }
    }

    @Override
    public void readHTTPChunk(HttpContent chunk) {
        respondWith(chunk);
    }

    @Override
    public void readRaw(ByteBuf buf) {
        clientConnection.write(buf);
    }

    /**
     * <p>
     * Responses to HEAD requests aren't supposed to have content, but Netty doesn't know that any given response is to
     * a HEAD request, so it needs to be told that there's no content so that it doesn't hang waiting for it.
     * </p>
     *
     * <p>
     * See the documentation for {@link HttpResponseDecoder} for information about why HEAD requests need special
     * handling.
     * </p>
     *
     * <p>
     * Thanks to <a href="https://github.com/nataliakoval">nataliakoval</a> for pointing out that with connections being
     * reused as they are, this needs to be sensitive to the current request.
     * </p>
     */
    private class HeadAwareHttpResponseDecoder extends HttpResponseDecoder {

        public HeadAwareHttpResponseDecoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
            super(maxInitialLineLength, maxHeaderSize, maxChunkSize);
        }

        @Override
        public boolean isContentAlwaysEmpty(HttpMessage httpMessage) {
            if (currentHttpRequest == null) {
                return true;
            } else {
                return ProxyUtils.isHEAD(currentHttpRequest) || super.isContentAlwaysEmpty(httpMessage);
            }
        }
    }

    ;

    /***************************************************************************
     * Writing
     **************************************************************************/

    /**
     * Like {@link #write(Object)} and also sets the current filters to the given value.
     *
     * @param msg
     * @param filters
     */
    public void write(Object msg, HttpFiltersAdapter filters) {
        this.currentFilters = filters;
        write(msg);
    }

    @Override
    public void write(Object msg) {
        LOG.debug("Requested write of {}", msg);

        if (msg instanceof ReferenceCounted) {
            LOG.debug("Retaining reference counted message");
            ((ReferenceCounted)msg).retain();
        }

        if (is(DISCONNECTED) && msg instanceof HttpRequest) {
            LOG.debug("Currently disconnected, connect and then write the message");
            connectAndWrite((HttpRequest)msg);
        } else {
            if (isConnecting()) {
                synchronized (connectLock) {
                    if (isConnecting()) {
                        LOG.debug(
                            "Attempted to write while still in the process of connecting, waiting for connection.");
                        clientConnection.stopReading();
                        try {
                            connectLock.wait(30000);
                        } catch (InterruptedException ie) {
                            LOG.warn("Interrupted while waiting for connect monitor");
                        }
                    }
                }
            }
            if (isConnecting() || getCurrentState().isDisconnectingOrDisconnected()) {
                LOG.debug(
                    "Connection failed or timed out while waiting to write message to server. Message will be discarded: {}",
                    msg);
                return;
            }
            LOG.debug("Using existing connection to: {}", remoteAddress);
            doWrite(msg);
        }
    }

    ;

    @Override
    public void writeHttp(HttpObject httpObject) {
        if (httpObject instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest)httpObject;
            currentHttpRequest = httpRequest;
        }
        super.writeHttp(httpObject);
    }

    /***************************************************************************
     * Lifecycle
     **************************************************************************/
    @Override
    public void become(ConnectionState newState) {
        if (getCurrentState() == DISCONNECTED && newState == CONNECTING) {
            currentFilters.proxyToServerConnectionStarted();
        } else if (getCurrentState() == CONNECTING) {
            if (newState == HANDSHAKING) {
                currentFilters.proxyToServerConnectionSSLHandshakeStarted();
            } else if (newState == AWAITING_INITIAL) {
                currentFilters.proxyToServerConnectionSucceeded(ctx);
            } else if (newState == DISCONNECTED) {
                currentFilters.proxyToServerConnectionFailed();
            }
        } else if (getCurrentState() == HANDSHAKING) {
            if (newState == AWAITING_INITIAL) {
                currentFilters.proxyToServerConnectionSucceeded(ctx);
            } else if (newState == DISCONNECTED) {
                currentFilters.proxyToServerConnectionFailed();
            }
        } else if (getCurrentState() == AWAITING_CHUNK && newState != AWAITING_CHUNK) {
            currentFilters.serverToProxyResponseReceived();
        }

        super.become(newState);
    }

    @Override
    public void becameSaturated() {
        super.becameSaturated();
        this.clientConnection.serverBecameSaturated(this);
    }

    @Override
    public void becameWritable() {
        super.becameWritable();
        this.clientConnection.serverBecameWriteable(this);
    }

    @Override
    public void timedOut() {
        super.timedOut();
        clientConnection.timedOut(this);
    }

    @Override
    public void disconnected() {
        super.disconnected();
        clientConnection.serverDisconnected(this);
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        try {
            if (cause instanceof IOException) {
                LOG.info("An IOException occurred on ProxyToServerConnection: " + cause.getMessage());
                LOG.debug("An IOException occurred on ProxyToServerConnection", cause);
            } else if (cause instanceof RejectedExecutionException) {
                LOG.info(
                    "An executor rejected a read or write operation on the ProxyToServerConnection (this is normal if the proxy is shutting down). Message: "
                        + cause.getMessage());
                LOG.debug("A RejectedExecutionException occurred on ProxyToServerConnection", cause);
            } else {
                LOG.error("Caught an exception on ProxyToServerConnection", cause);
            }
        } finally {
            if (!is(DISCONNECTED)) {
                LOG.info("Disconnecting open connection to server");
                disconnect();
            }
        }
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public String getServerHostAndPort() {
        return serverHostAndPort;
    }

    public HttpRequest getInitialRequest() {
        return initialRequest;
    }

    @Override
    public HttpFiltersAdapter getHttpFiltersFromProxyServer(HttpRequest httpRequest) {
        return currentFilters;
    }

    /***************************************************************************
     * Private Implementation
     **************************************************************************/

    /**
     * Keeps track of the current HttpResponse so that we can associate its headers with future related chunks for this
     * same transfer.
     *
     * @param response
     */
    private void rememberCurrentResponse(HttpResponse response) {
        LOG.debug("Remembering the current response.");
        currentHttpResponse = ProxyUtils.copyMutableResponseFields(response);
    }

    /**
     * Respond to the client with the given {@link HttpObject}.
     *
     * @param httpObject
     */
    private void respondWith(HttpObject httpObject) {
        clientConnection.respond(this, currentFilters, currentHttpRequest, currentHttpResponse, httpObject);
    }

    /**
     * Configures the connection to the upstream server and begins the {@link ConnectionFlow}.
     *
     * @param initialRequest
     *            the current HTTP request being handled
     */
    private void connectAndWrite(HttpRequest initialRequest) {
        LOG.debug("Starting new connection to: {}", remoteAddress);
        this.initialRequest = initialRequest;
        initializeConnectionFlow();
        connectionFlow.start();
    }

    /**
     * This method initializes our {@link ConnectionFlow} based on however this connection has been configured. If the
     * {@link #disableSni} value is true, this method will not pass peer information to the MitmManager when handling
     * CONNECTs.
     */
    private void initializeConnectionFlow() {
        this.connectionFlow = new ConnectionFlow(clientConnection, this, connectLock).then(connectChannel);
        if (ProxyUtils.isCONNECT(initialRequest)) {
            connectionFlow.then(serverConnection.StartTunneling).then(clientConnection.RespondCONNECTSuccessful)
                .then(clientConnection.StartTunneling);
        }
    }

    /**
     * Opens the socket connection.
     */
    private ConnectionFlowStep connectChannel = new ConnectionFlowStep(this, CONNECTING) {
        @Override
        public boolean shouldExecuteOnEventLoop() {
            return false;
        }

        @Override
        public Future<?> execute() {
            Bootstrap cb = new Bootstrap().group(proxyServer.getProxyToServerWorkerFor())//
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, proxyServer.getConnectTimeout())
                .handler(new ChannelInitializer<Channel>() {
                    public void initChannel(Channel ch) throws Exception {
                        Object tracingContext =
                            ProxyToServerConnection.this.clientConnection.channel.attr(KEY_CONTEXT).get();
                        ch.attr(KEY_CONTEXT).set(tracingContext);
                        initChannelPipeline(ch.pipeline(), initialRequest);
                    }

                ;
                });
            if (localAddress != null) {
                return cb.connect(remoteAddress, localAddress);
            } else {
                return cb.connect(remoteAddress);
            }
        }
    };

    public boolean connectionFailed(Throwable cause) throws UnknownHostException {
        if (!disableSni && cause instanceof SSLProtocolException) {
            if (cause.getMessage() != null && cause.getMessage().contains("unrecognized_name")) {
                LOG.debug(
                    "Failed to connect to server due to an unrecognized_name SSL warning. Retrying connection without SNI.");
                disableSni = true;
                resetConnectionForRetry();
                connectAndWrite(initialRequest);
                return true;
            }
        }
        disableSni = false;
        return false;
    }

    private void resetConnectionForRetry() throws UnknownHostException {
        this.ctx.pipeline().remove(this);
        this.ctx.close();
        this.ctx = null;
        this.setupConnectionParameters();
    }

    private void setupConnectionParameters() throws UnknownHostException {
        this.remoteAddress = this.currentFilters.proxyToServerResolutionStarted(serverHostAndPort);
        String hostAndPort = null;
        try {
            if (this.remoteAddress == null) {
                hostAndPort = serverHostAndPort;
                this.remoteAddress = addressFor(serverHostAndPort, proxyServer, enableSSL);
            } else if (this.remoteAddress.isUnresolved()) {
                hostAndPort =
                    HostAndPort.fromParts(this.remoteAddress.getHostName(), this.remoteAddress.getPort()).toString();
                this.remoteAddress = proxyServer.getServerResolver().resolve(this.remoteAddress.getHostName(),
                    this.remoteAddress.getPort());
            }
        } catch (UnknownHostException e) {
            this.currentFilters.proxyToServerResolutionFailed(hostAndPort);
            throw e;
        }
        this.currentFilters.proxyToServerResolutionSucceeded(serverHostAndPort, this.remoteAddress);
        this.localAddress = proxyServer.getLocalAddress();
    }

    private void initChannelPipeline(ChannelPipeline pipeline, HttpRequest httpRequest) {
        if (enableSSL && sslContext != null) {
            pipeline.addLast(sslContext.newHandler(pipeline.channel().alloc(), remoteAddress.getHostName(),
                remoteAddress.getPort()));
        }
        if (trafficHandler != null) {
            pipeline.addLast("global-traffic-shaping", trafficHandler);
        }
        pipeline.addLast("encoder", new HttpRequestEncoder());
        pipeline.addLast("decoder", new HeadAwareHttpResponseDecoder(proxyServer.getMaxInitialLineLength(),
            proxyServer.getMaxHeaderSize(), proxyServer.getMaxChunkSize()));
        int numberOfBytesToBuffer = proxyServer.getFiltersSource().getMaximumResponseBufferSizeInBytes();
        if (numberOfBytesToBuffer > 0) {
            aggregateContentForFiltering(pipeline, numberOfBytesToBuffer);
        }
        pipeline.addLast("idle", new IdleStateHandler(0, 0, proxyServer.getIdleConnectionTimeout()));
        pipeline.addLast("handler", this);
    }

    public void connectionSucceeded(boolean shouldForwardInitialRequest) {
        become(AWAITING_INITIAL);
        clientConnection.serverConnectionSucceeded(this, shouldForwardInitialRequest);
        if (shouldForwardInitialRequest) {
            LOG.debug("Writing initial request: {}", initialRequest);
            write(initialRequest);
        } else {
            LOG.debug("Dropping initial request: {}", initialRequest);
        }
        if (initialRequest instanceof ReferenceCounted) {
            ((ReferenceCounted)initialRequest).release();
        }
    }

    public static InetSocketAddress addressFor(String hostAndPort, HttpProxyServer proxyServer, boolean useSSL)
        throws UnknownHostException {
        HostAndPort parsedHostAndPort;
        try {
            parsedHostAndPort = HostAndPort.fromString(hostAndPort);
        } catch (IllegalArgumentException e) {
            throw new UnknownHostException(hostAndPort);
        }
        String host = parsedHostAndPort.getHost();
        int port;
        if (useSSL) {
            port = parsedHostAndPort.getPortOrDefault(443);
        } else {
            port = parsedHostAndPort.getPortOrDefault(80);
        }
        return proxyServer.getServerResolver().resolve(host, port);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
