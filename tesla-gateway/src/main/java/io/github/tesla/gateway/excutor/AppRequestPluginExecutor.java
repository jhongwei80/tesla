package io.github.tesla.gateway.excutor;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.support.plugins.AppKeyRequestPluginMetadata;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.gateway.protocol.springcloud.DynamicSpringCloudClient;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/23 13:52
 * @description:app key 过滤器会用到该cache
 */
public class AppRequestPluginExecutor extends CommonPluginExecutor {

    public AppRequestPluginExecutor() {

    }

    public AppRequestPluginExecutor(CommonPluginExecutor cache) {
        super();
        this.setFilterType(cache.getFilterType());
        this.setParamJson(cache.getParamJson());
        this.setFilterName(cache.getFilterName());
    }

    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject) {
        AbstractRequestPlugin appRequestFilter = null;
        try {
            appRequestFilter = AppKeyRequestPluginMetadata.getMetadataByType(filterType).getInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        appRequestFilter.setSpringCloudDiscovery(
            SpringContextHolder.getBean(DynamicSpringCloudClient.class).getSpringCloudDiscovery());
        return appRequestFilter.doFilter(servletRequest, realHttpObject, getParamJson());
    }

}
