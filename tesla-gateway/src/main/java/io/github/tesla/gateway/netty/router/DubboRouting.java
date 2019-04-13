package io.github.tesla.gateway.netty.router;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.endpoint.definition.DubboRpcRoutingDefinition;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.github.tesla.gateway.protocol.dubbo.DynamicDubboClient;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

public class DubboRouting {

    public static HttpResponse callRemote(NettyHttpServletRequest servletRequest, HttpObject httpObject,
        Object routerParamJson) {
        DubboRpcRoutingDefinition definition =
            JsonUtils.json2Definition(routerParamJson, DubboRpcRoutingDefinition.class);
        if (definition == null) {
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                "dubbo routing error");
        }
        if (definition != null && definition.getDubboParamTemplate() != null) {
            String jsonOutput =
                SpringContextHolder.getBean(DynamicDubboClient.class).doRpcRemoteCall(definition, servletRequest);
            HttpResponse response =
                PluginUtil.createResponse(HttpResponseStatus.OK, jsonOutput.getBytes(CharsetUtil.UTF_8));
            HttpUtil.setKeepAlive(response, false);
            return response;
        } else {
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                "dubbo routing error");
        }
    }

}
