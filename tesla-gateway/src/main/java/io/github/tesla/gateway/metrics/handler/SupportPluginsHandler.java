package io.github.tesla.gateway.metrics.handler;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import com.google.common.collect.Maps;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.github.tesla.auth.common.utils.JsonUtil;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.support.plugins.FilterMetadata;
import io.github.tesla.filter.utils.ClassUtils;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: zhangzhiping
 * @date: 2019/2/19 11:14
 * @description:
 */
public class SupportPluginsHandler implements HttpHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(SupportPluginsHandler.class);

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            parseGetParameters(httpExchange);
            parsePostParameters(httpExchange);
            Map<String, Object> params = (Map<String, Object>)httpExchange.getAttribute("parameters");
            String type = (String)params.get("type");
            Map<String, String> pluginsMap = Maps.newHashMap();
            switch (type) {
                case "endpoint":
                    pluginsMap.putAll(ClassUtils.findAllClasses(FilterMetadata.packageName, EndpointRequestPlugin.class)
                        .stream().map(clazz -> AnnotationUtils.findAnnotation(clazz, EndpointRequestPlugin.class))
                        .collect(
                            Collectors.toMap(EndpointRequestPlugin::filterType, EndpointRequestPlugin::filterName)));
                    pluginsMap.putAll(ClassUtils
                        .findAllClasses(FilterMetadata.packageName, EndpointResponsePlugin.class).stream()
                        .map(clazz -> AnnotationUtils.findAnnotation(clazz, EndpointResponsePlugin.class)).collect(
                            Collectors.toMap(EndpointResponsePlugin::filterType, EndpointResponsePlugin::filterName)));
                    break;
                default:
                    break;
            }
            String responseString = JsonUtil.toJsonString(pluginsMap);
            byte[] responseBytes = responseString.getBytes();
            httpExchange.sendResponseHeaders(HttpResponseStatus.OK.code(), responseBytes.length);
            OutputStream os = httpExchange.getResponseBody();
            os.write(responseBytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void parseGetParameters(HttpExchange exchange) throws UnsupportedEncodingException {

        Map<String, Object> parameters = new HashMap<String, Object>();
        URI requestedUri = exchange.getRequestURI();
        String query = requestedUri.getRawQuery();
        parseQuery(query, parameters);
        exchange.setAttribute("parameters", parameters);
    }

    private void parsePostParameters(HttpExchange exchange) throws IOException {

        if ("post".equalsIgnoreCase(exchange.getRequestMethod())) {
            Map<String, Object> parameters = (Map<String, Object>)exchange.getAttribute("parameters");
            InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
            BufferedReader br = new BufferedReader(isr);
            String query = br.readLine();
            parseQuery(query, parameters);
        }
    }

    private void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {
        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0], System.getProperty("file.encoding"));
                }
                if (param.length > 1) {
                    value = URLDecoder.decode(param[1], System.getProperty("file.encoding"));
                }
                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>)obj;
                        values.add(value);
                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String)obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }

}
