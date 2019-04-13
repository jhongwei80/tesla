package io.github.tesla.filter.service.plugin.request;

import static io.github.tesla.filter.support.CorsHandlerSupport.*;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.service.definition.CorsDefinition;
import io.github.tesla.filter.support.annnotation.ServiceRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cors.CorsConfig;

@ServiceRequestPlugin(filterType = "CorsRequestPlugin", definitionClazz = CorsDefinition.class, filterOrder = -100,
    filterName = "跨域支持插件")
public class CorsRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {

        CorsDefinition definition = JsonUtils.json2Definition(filterParam, CorsDefinition.class);

        if (definition == null) {
            return null;
        }
        CorsConfig config = buildCorsConfig(definition);

        HttpResponse httpResponse = null;
        if (config.isCorsSupportEnabled()) {
            HttpRequest request = servletRequest.getNettyRequest();
            if (isPreflightRequest(request)) {
                httpResponse = handlePreflight(request, config);
                if (httpResponse != null) {
                    return httpResponse;
                }
            }
            if (config.isShortCircuit() && !validateOrigin(config, request)) {
                httpResponse = forbidden(request);
                if (httpResponse != null) {
                    return httpResponse;
                }
            }
        }
        return httpResponse;
    }

}
