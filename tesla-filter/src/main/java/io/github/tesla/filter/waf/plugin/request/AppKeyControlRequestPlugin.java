package io.github.tesla.filter.waf.plugin.request;

import static io.github.tesla.filter.support.CorsHandlerSupport.isPreflightRequest;

import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.annnotation.WafRequestPlugin;
import io.github.tesla.filter.support.plugins.AppKeyRequestPluginMetadata;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.github.tesla.filter.waf.definition.AppKeyControlDefinition;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: zhangzhiping
 * @date: 2018/12/3 10:23
 * @description: 通过key对访问方做限制
 */
@WafRequestPlugin(filterType = "AppKeyControlRequestPlugin", definitionClazz = AppKeyControlDefinition.class,
    filterOrder = 7, filterName = "访问方限流及权限控制插件")
public class AppKeyControlRequestPlugin extends AbstractRequestPlugin {
    public static final String APP_KEY_HEADER = "X-Tesla-AccessKey";

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        HttpRequest request = servletRequest.getNettyRequest();
        if (isPreflightRequest(request)) {
            return null;
        }
        AppKeyControlDefinition definition = JsonUtils.json2Definition(filterParam, AppKeyControlDefinition.class);
        if (definition == null) {
            return null;
        }
        if (!MapUtils.isEmpty(definition.getIgnoreServices())) {
            for (String prefix : definition.getIgnoreServices().values())
                if (AntMatchUtil.matchPrefix(prefix, servletRequest.getRequestURI())) {
                    return null;
                }
        }
        HttpResponse httpResponse;
        String appKey = servletRequest.getHeader(APP_KEY_HEADER);
        if (StringUtils.isBlank(appKey) || getAppKeyMap(appKey) == null) {
            return PluginUtil.createResponse(HttpResponseStatus.UNAUTHORIZED, servletRequest.getNettyRequest(),
                " no access key or no match access key  , access reject ");
        }
        Map<String, String> appKeyParamMap = getAppKeyMap(appKey);
        for (String pluginType : appKeyParamMap.keySet()) {
            AbstractRequestPlugin requestPlugin = null;
            try {
                requestPlugin = AppKeyRequestPluginMetadata.getMetadataByType(pluginType).getInstance();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                    servletRequest.getNettyRequest(), e.getMessage());
            }
            httpResponse = requestPlugin.doFilter(servletRequest, realHttpObject, appKeyParamMap.get(pluginType));
            if (httpResponse != null) {
                return httpResponse;
            }
        }
        return null;
    }
}
