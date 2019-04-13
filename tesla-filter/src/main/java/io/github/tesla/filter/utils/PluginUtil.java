/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.filter.utils;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author liushiming
 * @version PluginUtil.java, v 0.0.1 2018年5月26日 上午1:08:29 liushiming
 */
public class PluginUtil {

    private static final Logger logger = LoggerFactory.getLogger("ProxyFilterLog");

    public static HttpRequest copy(HttpRequest original) {
        if (original instanceof FullHttpRequest) {
            return ((FullHttpRequest)original).copy();
        } else {
            HttpRequest request = new DefaultHttpRequest(original.protocolVersion(), original.method(), original.uri());
            request.headers().set(original.headers());
            return request;
        }
    }

    public static HttpResponse createResponse(HttpResponseStatus httpResponseStatus, byte[] content) {
        HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        if (content != null) {
            httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, content.length);
            ((DefaultFullHttpResponse)httpResponse).content().writeBytes(content);
        } else {
            httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO);
        }
        httpResponse.headers().add(httpHeaders);
        return httpResponse;
    }

    public static HttpResponse createResponse(HttpResponseStatus httpResponseStatus, HttpRequest originalRequest,
        byte[] reason) {
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        if (reason != null) {
            httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, reason.length);
            ((DefaultFullHttpResponse)httpResponse).content().writeBytes(reason);
        } else {
            httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO);
        }
        List<String> originHeader = PluginUtil.getHeaderValues(originalRequest, "Origin");
        if (originHeader.size() > 0) {
            httpHeaders.set("Access-Control-Allow-Credentials", "true");
            httpHeaders.set("Access-Control-Allow-Origin", originHeader.get(0));
        }
        httpResponse.headers().add(httpHeaders);
        return httpResponse;
    }

    public static HttpResponse createResponse(HttpResponseStatus httpResponseStatus, HttpRequest originalRequest,
        String reason) {
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        HttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, httpResponseStatus);
        if (StringUtils.isNotBlank(reason)) {
            httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, reason.getBytes(CharsetUtil.UTF_8).length);
            ((DefaultFullHttpResponse)httpResponse).content().writeBytes(reason.getBytes(CharsetUtil.UTF_8));
        } else {
            httpHeaders.set(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO);
        }
        List<String> originHeader = PluginUtil.getHeaderValues(originalRequest, "Origin");
        if (originHeader.size() > 0) {
            httpHeaders.set("Access-Control-Allow-Credentials", "true");
            httpHeaders.set("Access-Control-Allow-Origin", originHeader.get(0));
        }
        httpResponse.headers().add(httpHeaders);
        return httpResponse;
    }

    public static List<String> getHeaderValues(HttpMessage httpMessage, String headerName) {
        List<String> list = Lists.newArrayList();
        for (Map.Entry<String, String> header : httpMessage.headers().entries()) {
            if (header.getKey().toLowerCase().equals(headerName.toLowerCase())) {
                list.add(header.getValue());
            }
        }
        return list;
    }

    public static String getRealIp(HttpRequest httpRequest) {
        List<String> headerValues = getHeaderValues(httpRequest, "X-Real-IP");
        if (headerValues.size() > 0) {
            return headerValues.get(0);
        } else {
            return null;
        }

    }

    public static void writeFilterLog(Class<?> type, String reason, Throwable... cause) {
        if (cause != null && cause.length > 0) {
            logger.error("execute filter:" + type + " occur error, reason is:" + reason, cause[0]);
        } else {
            logger.info("execute filter:" + type + " occur error, reason is:" + reason);
        }
    }

    public static void resetRequestBody(FullHttpRequest realHttpObject, String requestBody) {
        ByteBuf bodyContent = Unpooled.copiedBuffer(requestBody, CharsetUtil.UTF_8);
        FullHttpRequest realRequest = realHttpObject;
        realRequest.content().clear().writeBytes(bodyContent);
        HttpUtil.setContentLength(realRequest, bodyContent.readerIndex());
    }
}
