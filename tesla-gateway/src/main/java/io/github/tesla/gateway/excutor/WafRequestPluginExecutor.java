package io.github.tesla.gateway.excutor;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.plugins.RequestPluginMetadata;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/29 16:56
 * @description:
 */
public class WafRequestPluginExecutor extends CommonPluginExecutor {

    private static final long serialVersionUID = 1L;

    private RequestPluginMetadata requestPluginEnum;

    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject) {
        AbstractRequestPlugin wafRequestFilter = null;
        try {
            wafRequestFilter = requestPluginEnum.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        wafRequestFilter.setSpringCloudDiscovery(
            SpringContextHolder.getBean(DynamicSpringCloudClient.class).getSpringCloudDiscovery());
        return wafRequestFilter.doFilter(servletRequest, realHttpObject, paramJson);
    }

    public RequestPluginMetadata getRequestPluginEnum() {
        return requestPluginEnum;
    }

    public void setRequestPluginEnum(RequestPluginMetadata requestPluginEnum) {
        this.requestPluginEnum = requestPluginEnum;
    }
}
