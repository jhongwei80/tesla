package io.github.tesla.filter.support;

import static io.netty.handler.codec.http.HttpMethod.OPTIONS;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.util.ReferenceCountUtil.release;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import io.github.tesla.filter.service.definition.CorsDefinition;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;

public class CorsHandlerSupport {
    private static final String ANY_ORIGIN = "*";
    private static final String NULL_ORIGIN = "null";
    private static final String ANY_HEADER = "*";

    private static final Logger LOGGER = LoggerFactory.getLogger(CorsHandlerSupport.class);

    public static CorsConfig buildCorsConfig(CorsDefinition definition) {
        HttpMethod[] allowedMethods = new HttpMethod[definition.getAllowedRequestMethods().length];
        for (int i = 0; i < definition.getAllowedRequestMethods().length; i++) {
            allowedMethods[i] = HttpMethod.valueOf(definition.getAllowedRequestMethods()[i]);
        }
        CorsConfigBuilder corsConfigBuilder;
        if (definition.getAllowedOrigins().contains(",")) {
            corsConfigBuilder = CorsConfigBuilder.forOrigins(definition.getAllowedOrigins().split(","));
        } else {
            corsConfigBuilder = CorsConfigBuilder.forOrigin(definition.getAllowedOrigins().trim());
        }
        corsConfigBuilder.allowedRequestMethods(allowedMethods)
            .allowedRequestHeaders(definition.getAllowedRequestHeaders().split(","));
        if (YesOrNoEnum.YES.getCode().equals(definition.getAllowCredentials())) {
            corsConfigBuilder.allowCredentials();
        }
        return corsConfigBuilder.build();
    }

    private static void echoRequestOrigin(HttpResponse response, HttpRequest request) {
        setOrigin(response, request.headers().get(HttpHeaderNames.ORIGIN));
    }

    public static HttpResponse forbidden(final HttpRequest request) {
        HttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), FORBIDDEN);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO);
        release(request);
        return response;
    }

    public static HttpResponse handlePreflight(final HttpRequest request, CorsConfig config) {
        final HttpResponse response = new DefaultFullHttpResponse(request.protocolVersion(), OK, true, true);
        if (setOrigin(response, request, config)) {
            setAllowMethods(response, config);
            setAllowHeaders(request, response, config);
            setAllowCredentials(response, config);
            setMaxAge(response, config);
            setPreflightHeaders(response, config);
        }
        if (!response.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, HttpHeaderValues.ZERO);
        }
        release(request);
        return response;
    }

    public static boolean isPreflightRequest(final HttpRequest request) {
        final HttpHeaders headers = request.headers();
        return request.method().equals(OPTIONS) && headers.contains(HttpHeaderNames.ORIGIN)
            && headers.contains(HttpHeaderNames.ACCESS_CONTROL_REQUEST_METHOD);
    }

    public static void setAllowCredentials(HttpResponse response, CorsConfig config) {
        if (config.isCredentialsAllowed()
            && !response.headers().get(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN).equals(ANY_ORIGIN)) {
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
    }

    private static void setAllowHeaders(HttpRequest request, HttpResponse response, CorsConfig config) {
        HttpHeaders headers = request.headers();
        List<String> allowHeaders = getHeadersIgnoreCase(headers, HttpHeaderNames.ACCESS_CONTROL_REQUEST_HEADERS);
        allowHeaders = checkHeaders(allowHeaders, config);
        if (allowHeaders != null) {
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, allowHeaders);
        }
    }

    private static List<String> getHeadersIgnoreCase(final HttpHeaders headers, CharSequence headerName) {
        return headers.entries().stream().filter(entry -> entry.getKey().equalsIgnoreCase(headerName.toString()))
            .flatMap(entry -> Arrays.stream(StringUtils.tokenizeToStringArray(entry.getValue(), ",")))
            .collect(Collectors.toList());
    }

    public static List<String> checkHeaders(List<String> requestHeaders, CorsConfig config) {
        if (requestHeaders == null) {
            return null;
        }
        if (requestHeaders.isEmpty()) {
            return Collections.emptyList();
        }
        if (ObjectUtils.isEmpty(config.allowedRequestHeaders())) {
            return null;
        }

        boolean allowAnyHeader = config.allowedRequestHeaders().contains(ANY_HEADER);
        List<String> result = new ArrayList<>(requestHeaders.size());
        for (String requestHeader : requestHeaders) {
            if (StringUtils.hasText(requestHeader)) {
                requestHeader = requestHeader.trim();
                if (allowAnyHeader) {
                    result.add(requestHeader);
                } else {
                    for (String allowedHeader : config.allowedRequestHeaders()) {
                        if (requestHeader.equalsIgnoreCase(allowedHeader)) {
                            result.add(requestHeader);
                            break;
                        }
                    }
                }
            }
        }
        return (result.isEmpty() ? null : result);
    }

    private static void setAllowMethods(HttpResponse response, CorsConfig config) {
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, config.allowedRequestMethods());
    }

    private static void setAnyOrigin(HttpResponse response) {
        setOrigin(response, ANY_ORIGIN);
    }

    public static void setExposeHeaders(final HttpResponse response, CorsConfig config) {
        if (!config.exposedHeaders().isEmpty()) {
            response.headers().set(HttpHeaderNames.ACCESS_CONTROL_EXPOSE_HEADERS, config.exposedHeaders());
        }
    }

    private static void setMaxAge(HttpResponse response, CorsConfig config) {
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, config.maxAge());
    }

    private static void setNullOrigin(HttpResponse response) {
        setOrigin(response, NULL_ORIGIN);
    }

    public static boolean setOrigin(HttpResponse response, HttpRequest request, CorsConfig config) {
        final String origin = request.headers().get(HttpHeaderNames.ORIGIN);
        if (origin != null) {
            if (NULL_ORIGIN.equals(origin) && config.isNullOriginAllowed()) {
                setNullOrigin(response);
                return true;
            }
            if (config.isAnyOriginSupported()) {
                if (config.isCredentialsAllowed()) {
                    echoRequestOrigin(response, request);
                    setVaryHeader(response);
                } else {
                    setAnyOrigin(response);
                }
                return true;
            }
            if (config.origins().contains(origin)) {
                setOrigin(response, origin);
                setVaryHeader(response);
                return true;
            }
            LOGGER.debug("Request origin [{}]] was not among the configured origins [{}]", origin, config.origins());
        }
        return false;
    }

    private static void setOrigin(HttpResponse response, final String origin) {
        response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, origin);
    }

    private static void setPreflightHeaders(HttpResponse response, CorsConfig config) {
        response.headers().add(config.preflightResponseHeaders());
    }

    private static void setVaryHeader(HttpResponse response) {
        response.headers().set(HttpHeaderNames.VARY, HttpHeaderNames.ORIGIN);
    }

    public static boolean validateOrigin(CorsConfig config, HttpRequest request) {
        if (config.isAnyOriginSupported()) {
            return true;
        }

        final String origin = request.headers().get(HttpHeaderNames.ORIGIN);
        if (origin == null) {
            // Not a CORS request so we cannot validate it. It may be a non CORS request.
            return true;
        }

        if ("null".equals(origin) && config.isNullOriginAllowed()) {
            return true;
        }

        return config.origins().contains(origin);
    }
}
