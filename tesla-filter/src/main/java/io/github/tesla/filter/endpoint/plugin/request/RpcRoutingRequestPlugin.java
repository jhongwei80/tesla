package io.github.tesla.filter.endpoint.plugin.request;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.RpcRoutingDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/28 15:45
 * @description:
 */
@EndpointRequestPlugin(filterType = "RpcRoutingRequestPlugin", definitionClazz = RpcRoutingDefinition.class,
    filterOrder = 8, filterName = "Rpc路由配置插件")
public class RpcRoutingRequestPlugin extends AbstractRequestPlugin {

    public static final String RPC_PARAM_JSON = "Osg-Rpc-Param-Json";

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        RpcRoutingDefinition definition = JsonUtils.json2Definition(filterParam, RpcRoutingDefinition.class);
        if (definition == null) {
            return null;
        }
        servletRequest.setAttribute(RPC_PARAM_JSON, definition.getRpcParamJson());
        return null;

    }
}
