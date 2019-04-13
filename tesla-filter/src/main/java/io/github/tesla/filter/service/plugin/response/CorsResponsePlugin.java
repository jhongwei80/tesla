package io.github.tesla.filter.service.plugin.response;

import static io.github.tesla.filter.support.CorsHandlerSupport.*;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.service.definition.CorsDefinition;
import io.github.tesla.filter.support.annnotation.ServiceResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.cors.CorsConfig;

@ServiceResponsePlugin(filterType = "CorsRequestPlugin", definitionClazz = CorsDefinition.class, filterOrder = -100,
    filterName = "跨域支持插件")
public class CorsResponsePlugin extends AbstractResponsePlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        CorsDefinition definition = JsonUtils.json2Definition(filterParam, CorsDefinition.class);
        if (definition == null) {
            return httpResponse;
        }
        CorsConfig config = buildCorsConfig(definition);

        if (config.isCorsSupportEnabled()) {
            if (setOrigin(httpResponse, servletRequest.getNettyRequest(), config)) {
                setAllowCredentials(httpResponse, config);
                setExposeHeaders(httpResponse, config);
            }
        }
        return httpResponse;
    }

}
