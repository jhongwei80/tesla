package io.github.tesla.gateway.excutor;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.support.plugins.ResponsePluginMetadata;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;
import io.netty.handler.codec.http.HttpResponse;

public class ServiceResponsePluginExecutor extends CommonPluginExecutor {

    private ResponsePluginMetadata responsePluginEnum;

    public ServiceResponsePluginExecutor() {

    }

    public ServiceResponsePluginExecutor(CommonPluginExecutor cache) {
        super();
        this.setFilterType(cache.getFilterType());
        this.setParamJson(cache.getParamJson());
        this.setFilterName(cache.getFilterName());
    }

    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse) {
        AbstractResponsePlugin apiResponseFilter = null;
        try {
            apiResponseFilter = responsePluginEnum.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        apiResponseFilter.setSpringCloudDiscovery(
            SpringContextHolder.getBean(DynamicSpringCloudClient.class).getSpringCloudDiscovery());
        return apiResponseFilter.doFilter(servletRequest, httpResponse, getParamJson());
    }

    public ResponsePluginMetadata getResponsePluginEnum() {
        return responsePluginEnum;
    }

    public void setResponsePluginEnum(ResponsePluginMetadata responsePluginEnum) {
        this.responsePluginEnum = responsePluginEnum;
    }
}
