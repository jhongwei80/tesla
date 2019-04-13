package io.github.tesla.gateway.excutor;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.support.plugins.ResponsePluginMetadata;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/12/3 11:51
 * @description:
 */
public class WafResponsePluginExecutor extends CommonPluginExecutor {

    private static final long serialVersionUID = 1L;

    private ResponsePluginMetadata responsePluginEnum;

    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse) {

        AbstractResponsePlugin wafResponseFilter = null;
        try {
            wafResponseFilter = responsePluginEnum.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        wafResponseFilter.setSpringCloudDiscovery(
            SpringContextHolder.getBean(DynamicSpringCloudClient.class).getSpringCloudDiscovery());
        return wafResponseFilter.doFilter(servletRequest, httpResponse, paramJson);
    }

    public ResponsePluginMetadata getResponsePluginEnum() {
        return responsePluginEnum;
    }

    public void setResponsePluginEnum(ResponsePluginMetadata responsePluginEnum) {
        this.responsePluginEnum = responsePluginEnum;
    }
}
