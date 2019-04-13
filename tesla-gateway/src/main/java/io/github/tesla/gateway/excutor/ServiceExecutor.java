package io.github.tesla.gateway.excutor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import io.github.tesla.filter.support.enums.HttpMethodEnum;
import io.github.tesla.filter.utils.AntMatchUtil;

/**
 * @author: zhangzhiping
 * @date: 2018/11/27 11:42
 * @description:
 */
public class ServiceExecutor implements Comparable<ServiceExecutor>, Serializable {

    private static final long serialVersionUID = 1L;

    private String servicePrefix;

    private List<EndpointExecutor> endPointDefinitionList;

    private ServiceRouterExecutor routerCache;

    @Override
    // 按长度倒叙排
    public int compareTo(ServiceExecutor o) {
        return o.servicePrefix.length() - this.servicePrefix.length();
    }

    public List<EndpointExecutor> getEndPointDefinitionList() {
        return endPointDefinitionList;
    }

    private EndpointExecutor getMatchEndpoint(String uri, String method) {
        for (EndpointExecutor endpointCache : getEndPointDefinitionList()) {
            String path = AntMatchUtil.replacePrefix(uri, servicePrefix, "");
            if (AntMatchUtil.match(endpointCache.getEndPointPath(), path)
                && HttpMethodEnum.match(endpointCache.getEndPointMethod(), method)) {
                return endpointCache;
            }
        }
        return null;
    }

    public ServiceRouterExecutor getRouterCache() {
        return routerCache;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public List<ServiceRequestPluginExecutor> matchAndGetRequestFiltes(String uri, String method) {
        EndpointExecutor endpointCache = getMatchEndpoint(uri, method);
        return endpointCache != null ? endpointCache.getRequestFiltersList() : null;
    }

    public List<ServiceResponsePluginExecutor> matchAndGetResponseFiltes(String uri, String method) {
        EndpointExecutor endpointCache = getMatchEndpoint(uri, method);
        return endpointCache != null ? endpointCache.getResponseFiltersList() : Collections.emptyList();
    }

    public void setEndPointDefinitionList(List<EndpointExecutor> endPointCacheList) {
        this.endPointDefinitionList = endPointCacheList;
    }

    public void setRouterCache(ServiceRouterExecutor routerCache) {
        this.routerCache = routerCache;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }
}
