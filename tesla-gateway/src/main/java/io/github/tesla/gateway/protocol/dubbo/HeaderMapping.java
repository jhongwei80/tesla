package io.github.tesla.gateway.protocol.dubbo;

import java.util.Enumeration;
import java.util.Map;

import com.google.common.collect.Maps;

import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;

public class HeaderMapping {

    private final Map<String, String> headers = Maps.newHashMap();

    private String method;

    private String uri;

    public HeaderMapping(NettyHttpServletRequest request) {
        Enumeration<String> e = request.getHeaderNames();
        while (e.hasMoreElements()) {
            String headerName = e.nextElement();
            Enumeration<String> headerValues = request.getHeaders(headerName);
            while (headerValues.hasMoreElements()) {
                headers.put(headerName, headerValues.nextElement());
            }
        }
        this.method = request.getMethod();
        this.uri = request.getRequestURI();
    }

    public String get(String headerKey) {
        return headers.get(headerKey);
    }

    public String method() {
        return this.method;
    }

    public String uri() {
        return this.uri;
    }
}
