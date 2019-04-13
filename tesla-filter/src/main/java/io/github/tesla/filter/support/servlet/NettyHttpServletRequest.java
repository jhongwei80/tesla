package io.github.tesla.filter.support.servlet;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.security.Principal;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.http.Cookie;

import io.github.tesla.filter.utils.ServletUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import io.netty.handler.ssl.SslHandler;

@SuppressWarnings("deprecation")
public class NettyHttpServletRequest implements HttpServletRequest {
    public static final String TRACING_SPAN_KEY = "tracing_span";
    private static final Locale DEFAULT_LOCALE = Locale.getDefault();
    private final FullHttpRequest request;
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private final QueryStringDecoder queryStringDecoder;
    private final Map<String, String[]> parameterMap;
    private final ChannelHandlerContext ctx;
    private String servletPath = "/";
    private String requestUri;
    private String pathInfo;
    private String queryString;
    private byte[] requestBody;

    public NettyHttpServletRequest(FullHttpRequest httpRequest, ChannelHandlerContext ctx) {
        this.request = httpRequest;
        this.ctx = ctx;
        this.queryStringDecoder = new QueryStringDecoder(this.request.uri());
        Map<String, List<String>> parameters = this.queryStringDecoder.parameters();
        parameterMap = new HashMap<String, String[]>(parameters.size());
        for (Map.Entry<String, List<String>> stringListEntry : parameters.entrySet()) {
            String[] strings = new String[stringListEntry.getValue().size()];
            parameterMap.put(stringListEntry.getKey(), stringListEntry.getValue().toArray(strings));
        }
        String contentType = request.headers().get(HttpHeaderNames.CONTENT_TYPE);
        if (httpRequest.method() == HttpMethod.POST && contentType != null) {
            String lowercased = contentType.toLowerCase();
            if (lowercased.startsWith(HttpHeaderValues.MULTIPART_FORM_DATA.toString())
                || lowercased.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString())) {
                HttpPostRequestDecoder decoder =
                    new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), httpRequest);
                List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
                for (InterfaceHttpData data : postData) {
                    if (data.getHttpDataType() == HttpDataType.Attribute) {
                        MemoryAttribute attribute = (MemoryAttribute)data;
                        String key = attribute.getName();
                        String value = attribute.getValue();
                        parameterMap.put(key, new String[] {value});
                    }
                }
            }
        }
        this.checkAndParsePaths(this.request.uri());
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    public void checkAndParsePaths(String uri) {
        int indx = uri.indexOf('?');
        if (indx != -1) {
            this.pathInfo = uri.substring(servletPath.length(), indx);
            this.queryString = uri.substring(indx + 1);
            this.requestUri = uri.substring(0, indx);
        } else {
            this.pathInfo = uri.substring(servletPath.length());
            this.requestUri = uri;
        }
        if (this.pathInfo.equals("")) {
            this.pathInfo = null;
        } else if (!this.pathInfo.startsWith("/")) {
            this.pathInfo = "/" + this.pathInfo;
        }
    }

    @Override
    public AsyncContext getAsyncContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getAttribute(String name) {
        synchronized (attributes) {
            return attributes.get(name);
        }
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        synchronized (attributes) {
            return Collections.enumeration(attributes.keySet());
        }
    }

    @Override
    public String getAuthType() {
        return getHeader(WWW_AUTHENTICATE);
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getContentLength() {
        return (int)HttpHeaders.getContentLength(this.request, -1);
    }

    @Override
    public String getContentType() {
        return HttpHeaders.getHeader(this.request, Names.CONTENT_TYPE);
    }

    @Override
    public String getContextPath() {
        String requestURI = getRequestURI();
        return "/".equals(requestURI) ? "" : requestURI;
    }

    @Override
    public Cookie[] getCookies() {
        String cookieString = this.request.headers().get(COOKIE);
        if (cookieString != null) {
            Set<io.netty.handler.codec.http.Cookie> cookies = CookieDecoder.decode(cookieString);
            if (!cookies.isEmpty()) {
                Cookie[] cookiesArray = new Cookie[cookies.size()];
                int indx = 0;
                for (io.netty.handler.codec.http.Cookie c : cookies) {
                    Cookie cookie = new Cookie(c.getName(), c.getValue());
                    cookie.setComment(c.getComment());
                    if (c.getDomain() != null)
                        cookie.setDomain(c.getDomain());
                    cookie.setMaxAge((int)c.getMaxAge());
                    cookie.setPath(c.getPath());
                    cookie.setSecure(c.isSecure());
                    cookie.setVersion(c.getVersion());
                    cookiesArray[indx] = cookie;
                    indx++;
                }
                return cookiesArray;

            }
        }
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        String longVal = getHeader(name);
        if (longVal == null)
            return -1;
        return Long.parseLong(longVal);
    }

    @Override
    public DispatcherType getDispatcherType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHeader(String name) {
        return HttpHeaders.getHeader(this.request, name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return ServletUtil.enumeration(this.request.headers().names());
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return ServletUtil.enumeration(this.request.headers().getAll(name));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.request instanceof FullHttpRequest) {
            FullHttpRequest httprequest = this.request;
            return new ByteBufferServletInputStream(httprequest.content());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public int getIntHeader(String name) {
        return HttpHeaders.getIntHeader(this.request, name, -1);
    }

    @Override
    public String getLocalAddr() {
        final Channel channel = this.ctx.channel();
        InetSocketAddress addr = (InetSocketAddress)channel.localAddress();
        return addr.getAddress().getHostAddress();
    }

    @Override
    public Locale getLocale() {
        String locale = HttpHeaders.getHeader(this.request, Names.ACCEPT_LANGUAGE, DEFAULT_LOCALE.toString());
        return new Locale(locale);
    }

    @Override
    public Enumeration<Locale> getLocales() {
        Collection<Locale> locales =
            ServletUtil.parseAcceptLanguageHeader(HttpHeaders.getHeader(this.request, Names.ACCEPT_LANGUAGE));

        if (locales == null || locales.isEmpty()) {
            locales = new ArrayList<Locale>();
            locales.add(Locale.getDefault());
        }
        return ServletUtil.enumeration(locales);
    }

    @Override
    public String getLocalName() {
        return getServerName();
    }

    @Override
    public int getLocalPort() {
        return getServerPort();
    }

    @Override
    public String getMethod() {
        return this.request.method().name();
    }

    public FullHttpRequest getNettyRequest() {
        return this.request;
    }

    @Override
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        return values != null ? values[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return parameterMap;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return ServletUtil.enumeration(getParameterMap().keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return getParameterMap().get(name);
    }

    @Override
    public Part getPart(String name) throws IOException, IllegalStateException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPathInfo() {
        return this.pathInfo;
    }

    @Override
    public String getPathTranslated() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
        return request.protocolVersion().protocolName();
    }

    @Override
    public String getQueryString() {
        return this.queryString;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public String getRealPath(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        final Channel channel = this.ctx.channel();
        InetSocketAddress addr = (InetSocketAddress)channel.remoteAddress();
        return addr.getHostName();
    }

    @Override
    public int getRemotePort() {
        final Channel channel = this.ctx.channel();
        InetSocketAddress addr = (InetSocketAddress)channel.remoteAddress();
        return addr.getPort();
    }

    @Override
    public String getRemoteUser() {
        return getHeader(AUTHORIZATION);
    }

    public byte[] getRequestBody() throws IOException {
        if (this.requestBody != null) {
            return this.requestBody;
        }
        int contentLength = getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte[] buffer = new byte[contentLength];
        for (int i = 0; i < contentLength;) {
            int readlen = getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        this.requestBody = buffer;
        return buffer;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestedSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getRequestURI() {
        return this.requestUri;
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer url = new StringBuffer();
        String scheme = this.getScheme();
        int port = this.getServerPort();
        String urlPath = this.getRequestURI();
        url.append(scheme);
        url.append("://");
        url.append(this.getServerName());
        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            url.append(':');
            url.append(this.getServerPort());
        }
        url.append(urlPath);
        return url;
    }

    @Override
    public String getScheme() {
        try {
            return this.isSecure() ? "https" : "http";
        } catch (Exception e) {
            return "http";
        }
    }

    @Override
    public String getServerName() {
        final Channel channel = this.ctx.channel();
        if (Objects.nonNull(channel)) {
            final Optional<SocketAddress> socketAddress = Optional.ofNullable(channel.localAddress());
            if (socketAddress.isPresent()) {
                InetSocketAddress addr = (InetSocketAddress)socketAddress.get();
                return addr.getHostName();
            }
        }
        return "";
    }

    @Override
    public int getServerPort() {
        final Channel channel = this.ctx.channel();
        InetSocketAddress addr = (InetSocketAddress)channel.localAddress();
        return addr.getPort();
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getServletPath() {
        return this.servletPath;
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Principal getUserPrincipal() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncStarted() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAsyncSupported() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return true;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSecure() {
        final Channel channel = this.ctx.channel();
        return channel.pipeline().get(SslHandler.class) != null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void logout() throws ServletException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) {
        synchronized (attributes) {
            attributes.remove(name);
        }
    }

    @Override
    public void setAttribute(String name, Object o) {
        synchronized (attributes) {
            attributes.put(name, o);
        }
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
        throws IllegalStateException {
        throw new UnsupportedOperationException();
    }

}
