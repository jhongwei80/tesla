package io.github.tesla.gateway.netty.transmit.connection;

import static io.github.tesla.gateway.netty.transmit.ConnectionState.*;

import io.github.tesla.filter.utils.ProxyUtils;
import io.github.tesla.gateway.netty.HttpFiltersAdapter;
import io.github.tesla.gateway.netty.HttpProxyServer;
import io.github.tesla.gateway.netty.transmit.ConnectionState;
import io.github.tesla.gateway.netty.transmit.flow.ConnectionFlow;
import io.github.tesla.gateway.netty.transmit.flow.ConnectionFlowStep;
import io.github.tesla.gateway.netty.transmit.support.ProxyConnectionLogger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;

/**
 * <p>
 * Base class for objects that represent a connection to/from our proxy.
 * </p>
 * <p>
 * A ProxyConnection models a bidirectional message flow on top of a Netty {@link Channel}.
 * </p>
 * <p>
 * The {@link #read(Object)} method is called whenever a new message arrives on the underlying socket.
 * </p>
 * <p>
 * The {@link #write(Object)} method can be called by anyone wanting to write data out of the connection.
 * </p>
 * <p>
 * ProxyConnection has a lifecycle and its current state within that lifecycle is recorded as a {@link ConnectionState}.
 * The allowed states and transitions vary a little depending on the concrete implementation of ProxyConnection.
 * However, all ProxyConnections share the following lifecycle events:
 * </p>
 *
 * <ul>
 * <li>{@link #connected()} - Once the underlying channel is active, the ProxyConnection is considered connected and
 * moves into {@link ConnectionState#AWAITING_INITIAL}. The Channel is recorded at this time for later referencing.</li>
 * <li>{@link #disconnected()} - When the underlying channel goes inactive, the ProxyConnection moves into
 * {@link ConnectionState#DISCONNECTED}</li>
 * <li>{@link #becameWritable()} - When the underlying channel becomes writeable, this callback is invoked.</li>
 * </ul>
 *
 * <p>
 * By default, incoming data on the underlying channel is automatically read and passed to the {@link #read(Object)}
 * method. Reading can be stopped and resumed using {@link #stopReading()} and {@link #resumeReading()}.
 * </p>
 *
 * @param <I>
 *            the type of "initial" message. This will be either {@link HttpResponse} or {@link HttpRequest}.
 */
public abstract class ProxyConnection<I extends HttpObject> extends SimpleChannelInboundHandler<Object> {
    public final ProxyConnectionLogger LOG = new ProxyConnectionLogger(this);

    public final HttpProxyServer proxyServer;
    public volatile ChannelHandlerContext ctx;
    public volatile Channel channel;
    private volatile ConnectionState currentState;
    private volatile boolean tunneling = false;
    public volatile long lastReadTime = 0;

    /**
     * Construct a new ProxyConnection.
     *
     * @param initialState
     *            the state in which this connection starts out
     * @param proxyServer
     *            the {@link HttpProxyServer} in which we're running
     * @param runsAsSslClient
     *            determines whether this connection acts as an SSL client or server (determines who does the handshake)
     */
    public ProxyConnection(ConnectionState initialState, HttpProxyServer proxyServer) {
        become(initialState);
        this.proxyServer = proxyServer;
    }

    /***************************************************************************
     * Reading
     **************************************************************************/

    /**
     * Read is invoked automatically by Netty as messages arrive on the socket.
     *
     * @param msg
     */
    public void read(Object msg) {
        LOG.debug("Reading: {}", msg);
        lastReadTime = System.currentTimeMillis();
        if (tunneling) {
            readRaw((ByteBuf)msg);
        } else {
            readHTTP((HttpObject)msg);
        }
    }

    @SuppressWarnings("unchecked")
    private void readHTTP(HttpObject httpObject) {
        ConnectionState nextState = getCurrentState();
        switch (getCurrentState()) {
            case AWAITING_INITIAL:
                if (httpObject instanceof HttpMessage) {
                    nextState = readHTTPInitial((I)httpObject);
                } else {
                    LOG.debug(
                        "Dropping message because HTTP object was not an HttpMessage. HTTP object may be orphaned content from a short-circuited response. Message: {}",
                        httpObject);
                }
                break;
            case AWAITING_CHUNK:
                HttpContent chunk = (HttpContent)httpObject;
                readHTTPChunk(chunk);
                nextState = ProxyUtils.isLastChunk(chunk) ? AWAITING_INITIAL : AWAITING_CHUNK;
                break;
            case AWAITING_PROXY_AUTHENTICATION:
                if (httpObject instanceof HttpRequest) {
                    nextState = readHTTPInitial((I)httpObject);
                } else {
                }
                break;
            case CONNECTING:
                LOG.warn(
                    "Attempted to read from connection that's in the process of connecting.  This shouldn't happen.");
                break;
            case NEGOTIATING_CONNECT:
                LOG.debug(
                    "Attempted to read from connection that's in the process of negotiating an HTTP CONNECT.  This is probably the LastHttpContent of a chunked CONNECT.");
                break;
            case AWAITING_CONNECT_OK:
                LOG.warn("AWAITING_CONNECT_OK should have been handled by ProxyToServerConnection.read()");
                break;
            case HANDSHAKING:
                LOG.warn(
                    "Attempted to read from connection that's in the process of handshaking.  This shouldn't happen.",
                    channel);
                break;
            case DISCONNECT_REQUESTED:
            case DISCONNECTED:
                LOG.info("Ignoring message since the connection is closed or about to close");
                break;
        }
        become(nextState);
    }

    /**
     * Implement this to handle reading the initial object (e.g. {@link HttpRequest} or {@link HttpResponse}).
     *
     * @param httpObject
     * @return
     */
    public abstract ConnectionState readHTTPInitial(I httpObject);

    /**
     * Implement this to handle reading a chunk in a chunked transfer.
     *
     * @param chunk
     */
    public abstract void readHTTPChunk(HttpContent chunk);

    /**
     * Implement this to handle reading a raw buffer as they are used in HTTP tunneling.
     *
     * @param buf
     */
    public abstract void readRaw(ByteBuf buf);

    /***************************************************************************
     * Writing
     **************************************************************************/

    /**
     * This method is called by users of the ProxyConnection to send stuff out over the socket.
     *
     * @param msg
     */
    public void write(Object msg) {
        if (msg instanceof ReferenceCounted) {
            LOG.debug("Retaining reference counted message");
            ((ReferenceCounted)msg).retain();
        }

        doWrite(msg);
    }

    public void doWrite(Object msg) {
        LOG.debug("Writing: {}", msg);

        try {
            if (msg instanceof HttpObject) {
                writeHttp((HttpObject)msg);
            } else {
                writeRaw((ByteBuf)msg);
            }
        } finally {
            LOG.debug("Wrote: {}", msg);
        }
    }

    /**
     * Writes HttpObjects to the connection asynchronously.
     *
     * @param httpObject
     */
    public void writeHttp(HttpObject httpObject) {
        if (ProxyUtils.isLastChunk(httpObject)) {
            channel.write(httpObject);
            LOG.debug("Writing an empty buffer to signal the end of our chunked transfer");
            writeToChannel(Unpooled.EMPTY_BUFFER);
        } else {
            writeToChannel(httpObject);
        }
    }

    /**
     * Writes raw buffers to the connection.
     *
     * @param buf
     */
    public void writeRaw(ByteBuf buf) {
        writeToChannel(buf);
    }

    public ChannelFuture writeToChannel(final Object msg) {
        return channel.writeAndFlush(msg);
    }

    /***************************************************************************
     * Lifecycle
     **************************************************************************/

    /**
     * This method is called as soon as the underlying {@link Channel} is connected. Note that for proxies with complex
     * {@link ConnectionFlow}s that include SSL handshaking and other such things, just because the {@link Channel} is
     * connected doesn't mean that our connection is fully established.
     */
    public void connected() {
        LOG.debug("Connected");
    }

    /**
     * This method is called as soon as the underlying {@link Channel} becomes disconnected.
     */
    public void disconnected() {
        become(DISCONNECTED);
        LOG.debug("Disconnected");
    }

    /**
     * This method is called when the underlying {@link Channel} times out due to an idle timeout.
     */
    public void timedOut() {
        disconnect();
    }

    /**
     * <p>
     * Enables tunneling on this connection by dropping the HTTP related encoders and decoders, as well as idle timers.
     * </p>
     *
     * <p>
     * Note - the work is done on the {@link ChannelHandlerContext}'s executor because
     * {@link ChannelPipeline#remove(String)} can deadlock if called directly.
     * </p>
     */
    public ConnectionFlowStep StartTunneling = new ConnectionFlowStep(this, NEGOTIATING_CONNECT) {
        @Override
        public boolean shouldSuppressInitialRequest() {
            return true;
        }

        public Future<?> execute() {
            try {
                ChannelPipeline pipeline = ctx.pipeline();
                if (pipeline.get("encoder") != null) {
                    pipeline.remove("encoder");
                }
                if (pipeline.get("decoder") != null) {
                    pipeline.remove("decoder");
                }
                tunneling = true;
                return channel.newSucceededFuture();
            } catch (Throwable t) {
                return channel.newFailedFuture(t);
            }
        }
    };

    /**
     * Enables decompression and aggregation of content, which is useful for certain types of filtering activity.
     *
     * @param pipeline
     * @param numberOfBytesToBuffer
     */
    public void aggregateContentForFiltering(ChannelPipeline pipeline, int numberOfBytesToBuffer) {
        pipeline.addLast("inflater", new HttpContentDecompressor());
        pipeline.addLast("aggregator", new HttpObjectAggregator(numberOfBytesToBuffer));
    }

    /**
     * Callback that's invoked if this connection becomes saturated.
     */
    public void becameSaturated() {
        LOG.debug("Became saturated");
    }

    /**
     * Callback that's invoked when this connection becomes writeable again.
     */
    public void becameWritable() {
        LOG.debug("Became writeable");
    }

    /**
     * Override this to handle exceptions that occurred during asynchronous processing on the {@link Channel}.
     *
     * @param cause
     */
    public void exceptionCaught(Throwable cause) {}

    /***************************************************************************
     * State/Management
     **************************************************************************/
    /**
     * Disconnects. This will wait for pending writes to be flushed before disconnecting.
     *
     * @return Future<Void> for when we're done disconnecting. If we weren't connected, this returns null.
     */
    public Future<Void> disconnect() {
        if (channel == null) {
            return null;
        } else {
            final Promise<Void> promise = channel.newPromise();
            writeToChannel(Unpooled.EMPTY_BUFFER).addListener(new GenericFutureListener<Future<? super Void>>() {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception {
                    closeChannel(promise);
                }
            });
            return promise;
        }
    }

    private void closeChannel(final Promise<Void> promise) {
        channel.close().addListener(new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    promise.setSuccess(null);
                } else {
                    promise.setFailure(future.cause());
                }
            }

            ;
        });
    }

    /**
     * Indicates whether or not this connection is saturated (i.e. not writeable).
     *
     * @return
     */
    public boolean isSaturated() {
        return !this.channel.isWritable();
    }

    /**
     * Utility for checking current state.
     *
     * @param state
     * @return
     */
    public boolean is(ConnectionState state) {
        return currentState == state;
    }

    /**
     * If this connection is currently in the process of going through a {@link ConnectionFlow}, this will return true.
     *
     * @return
     */
    public boolean isConnecting() {
        return currentState.isPartOfConnectionFlow();
    }

    /**
     * Udpates the current state to the given value.
     *
     * @param state
     */
    public void become(ConnectionState state) {
        this.currentState = state;
    }

    public ConnectionState getCurrentState() {
        return currentState;
    }

    public boolean isTunneling() {
        return tunneling;
    }

    /**
     * Call this to stop reading.
     */
    public void stopReading() {
        LOG.debug("Stopped reading");
        this.channel.config().setAutoRead(false);
    }

    /**
     * Call this to resume reading.
     */
    public void resumeReading() {
        LOG.debug("Resumed reading");
        this.channel.config().setAutoRead(true);
    }

    /**
     * Request the ProxyServer for Filters.
     * <p>
     * By default, no-op filters are returned by DefaultHttpProxyServer. Subclasses of ProxyConnection can change this
     * behaviour.
     *
     * @param httpRequest
     *            Filter attached to the give HttpRequest (if any)
     * @return
     */
    public HttpFiltersAdapter getHttpFiltersFromProxyServer(HttpRequest httpRequest) {
        return proxyServer.getFiltersSource().filterRequest(httpRequest, ctx);
    }

    public ProxyConnectionLogger getLOG() {
        return LOG;
    }

    /***************************************************************************
     * Adapting the Netty API
     **************************************************************************/
    @Override
    public final void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        read(msg);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        try {
            this.ctx = ctx;
            this.channel = ctx.channel();
            this.proxyServer.registerChannel(ctx.channel());
        } finally {
            super.channelRegistered(ctx);
        }
    }

    @Override
    public final void channelActive(ChannelHandlerContext ctx) throws Exception {
        try {
            connected();
        } finally {
            super.channelActive(ctx);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        try {
            disconnected();
        } finally {
            super.channelInactive(ctx);
        }
    }

    @Override
    public final void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        LOG.debug("Writability changed. Is writable: {}", channel.isWritable());
        try {
            if (this.channel.isWritable()) {
                becameWritable();
            } else {
                becameSaturated();
            }
        } finally {
            super.channelWritabilityChanged(ctx);
        }
    }

    @Override
    public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        exceptionCaught(cause);
    }

    @Override
    public final void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        try {
            if (evt instanceof IdleStateEvent) {
                LOG.debug("Got idle");
                timedOut();
            }
        } finally {
            super.userEventTriggered(ctx, evt);
        }
    }
}
