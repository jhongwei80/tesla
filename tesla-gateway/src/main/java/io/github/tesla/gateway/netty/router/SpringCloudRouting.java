package io.github.tesla.gateway.netty.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.service.definition.SpringCloudRoutingDefinition;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.github.tesla.filter.utils.ProxyUtils;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;
import io.netty.handler.codec.http.*;

public class SpringCloudRouting {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringCloudRouting.class);

    public static HttpResponse callRemote(NettyHttpServletRequest servletRequest, HttpObject httpObject,
        Object paramJson) {
        SpringCloudRoutingDefinition definition =
            JsonUtils.json2Definition(paramJson, SpringCloudRoutingDefinition.class);
        if (definition == null) {
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                "spring routing error");
        }
        DynamicSpringCloudClient springCloudClient = SpringContextHolder.getBean(DynamicSpringCloudClient.class);
        String loadBalanceHostAndPort = springCloudClient.loadBalanceCall(definition.getServiceName(),
            definition.getGroup(), definition.getVersion(), servletRequest);
        final FullHttpRequest realRequest = (FullHttpRequest)httpObject;
        realRequest.headers().set(HttpHeaderNames.HOST, ProxyUtils.parseHostAndPort(loadBalanceHostAndPort));

        String changedPath =
            AntMatchUtil.replacePrefix(realRequest.uri(), definition.getServicePrefix(), definition.getTargetPrefix());
        LOGGER.info("SpringCloud process! request url:" + servletRequest.getRequestURI() + ";targetHost:"
            + ProxyUtils.parseHostAndPort(loadBalanceHostAndPort) + ";targetPath:" + changedPath);
        realRequest.setUri(changedPath);
        return null;
    }
}
