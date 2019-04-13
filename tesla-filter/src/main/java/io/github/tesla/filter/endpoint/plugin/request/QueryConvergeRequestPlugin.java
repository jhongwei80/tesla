package io.github.tesla.filter.endpoint.plugin.request;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.QueryConvergeDefinition;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@EndpointRequestPlugin(filterType = "QueryConvergeRequestPlugin", definitionClazz = QueryConvergeDefinition.class,
    filterOrder = 12, filterName = "聚合查询插件")
public class QueryConvergeRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        QueryConvergeDefinition definition = JsonUtils.json2Definition(filterParam, QueryConvergeDefinition.class);
        if (definition == null) {
            return null;
        }
        FullHttpRequest realRequest = (FullHttpRequest)realHttpObject;
        List<Map<String, String>> splitRequestInfos = Lists.newArrayList();
        definition.getRouterList().forEach(router -> {
            Map<String, String> routerMap = Maps.newHashMap();
            router.setRouterToMap(getSpringCloudDiscovery(), routerMap);
            router.setChangedPathToMap(realRequest.uri(), routerMap);
            splitRequestInfos.add(routerMap);
        });
        servletRequest.setAttribute(PluginDefinition.CONVERGE_ATTR_KEY, splitRequestInfos);
        return null;
    }
}
