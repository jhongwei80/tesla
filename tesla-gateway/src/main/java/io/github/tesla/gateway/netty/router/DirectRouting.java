package io.github.tesla.gateway.netty.router;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.filter.service.definition.DirectRoutingDefinition;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.github.tesla.filter.utils.ProxyUtils;
import io.netty.handler.codec.http.*;

public class DirectRouting {

    private static final Logger LOGGER = LoggerFactory.getLogger(DirectRouting.class);

    public static HttpResponse callRemote(NettyHttpServletRequest servletRequest, HttpObject httpObject,
        Object paramJson) {
        DirectRoutingDefinition definition = JsonUtils.json2Definition(paramJson, DirectRoutingDefinition.class);
        if (definition == null) {
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                "direct routing error");
        }
        final FullHttpRequest realRequest = (FullHttpRequest)httpObject;
        realRequest.headers().set(HttpHeaderNames.HOST, ProxyUtils.parseHostAndPort(definition.getTargetHostPort()));

        String changedPath =
            AntMatchUtil.replacePrefix(realRequest.uri(), definition.getServicePrefix(), definition.getTargetPrefix());
        LOGGER.info("Direct process! request url:" + servletRequest.getRequestURI() + ";targetHost:"
            + ProxyUtils.parseHostAndPort(definition.getTargetHostPort()) + ";targetPath:" + changedPath);
        realRequest.setUri(changedPath);
        if (YesOrNoEnum.YES.getCode().equals(definition.getEnableSSL())) {
            realRequest.headers().add(PluginDefinition.X_TESLA_ENABLE_SSL, definition.getEnableSSL());
            if (StringUtils.isNotBlank(definition.getSelfSignCrtFileId())) {
                realRequest.headers().add(PluginDefinition.X_TESLA_SELF_SIGN_CRT, definition.getSelfSignCrtFileId());
            }
        }
        return null;
    }
}
