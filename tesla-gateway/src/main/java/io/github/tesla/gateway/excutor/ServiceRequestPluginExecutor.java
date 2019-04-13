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
 * @date: 2018/11/29 16:39
 * @description:
 */
public class ServiceRequestPluginExecutor extends CommonPluginExecutor {

    private RequestPluginMetadata requestPluginEnum;

    public ServiceRequestPluginExecutor() {

    }

    public ServiceRequestPluginExecutor(CommonPluginExecutor cache) {
        super();
        this.setFilterType(cache.getFilterType());
        this.setParamJson(cache.getParamJson());
        this.setFilterName(cache.getFilterName());
    }

    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject) {
        AbstractRequestPlugin apiRequestFilter = null;
        try {
            apiRequestFilter = requestPluginEnum.getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        apiRequestFilter.setSpringCloudDiscovery(
            SpringContextHolder.getBean(DynamicSpringCloudClient.class).getSpringCloudDiscovery());
        return apiRequestFilter.doFilter(servletRequest, realHttpObject, getParamJson());
    }

    public RequestPluginMetadata getRequestPluginEnum() {
        return requestPluginEnum;
    }

    public void setRequestPluginEnum(RequestPluginMetadata requestPluginEnum) {
        this.requestPluginEnum = requestPluginEnum;
    }
}
