package io.github.tesla.filter.endpoint.definition;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.netflix.appinfo.InstanceInfo;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.common.dto.ServiceRouterDTO;
import io.github.tesla.filter.AbstractPlugin;
import io.github.tesla.filter.service.definition.DirectRoutingDefinition;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.service.definition.SpringCloudRoutingDefinition;
import io.github.tesla.filter.support.enums.RouteTypeEnum;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.springcloud.DiscoveryClientWrapper;
import io.github.tesla.filter.support.springcloud.SpringCloudDiscovery;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.ProxyUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class QueryConvergeDefinition extends PluginDefinition {

    private List<QueryConvergeRouter> routerList;

    public List<QueryConvergeRouter> getRouterList() {
        return routerList;
    }

    public void setRouterList(List<QueryConvergeRouter> routerList) {
        this.routerList = routerList;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        Set<String> repeatTag = Sets.newHashSet();
        QueryConvergeDefinition definition = JsonUtils.fromJson(paramJson, QueryConvergeDefinition.class);
        Preconditions.checkArgument(definition.getRouterList() != null && definition.getRouterList().size() > 0,
            "路由不可为空");
        definition.getRouterList().forEach(router -> {
            Preconditions.checkArgument(StringUtils.isNotBlank(router.getPatternPath()), "path转换表达式不可为空");
            router.setServicePrefix(serviceDTO.getServicePrefix());
            router.setTransformPath(endpointDTO.getEndpointUrl());
            Preconditions.checkArgument(
                StringUtils.isBlank(router.getConvergeTag()) || !repeatTag.contains(router.getConvergeTag()),
                "聚合标签不可重复：" + router.getConvergeTag());
            if (StringUtils.isNotBlank(router.getConvergeTag())) {
                repeatTag.add(router.getConvergeTag());
            }
        });
        return JsonUtils.serializeToJson(definition);
    }

    public class QueryConvergeRouter {

        private String serviceId;

        private String patternPath;

        private String transformPath;

        private String servicePrefix;

        private String convergeTag;

        public String getPatternPath() {
            return patternPath;
        }

        public void setPatternPath(String patternPath) {
            this.patternPath = patternPath;
        }

        public String getServicePrefix() {
            return servicePrefix;
        }

        public void setServicePrefix(String servicePrefix) {
            this.servicePrefix = servicePrefix;
        }

        public String getTransformPath() {
            return transformPath;
        }

        public void setTransformPath(String transformPath) {
            this.transformPath = transformPath;
        }

        public String getServiceId() {
            return serviceId;
        }

        public void setServiceId(String serviceId) {
            this.serviceId = serviceId;
        }

        public String getConvergeTag() {
            return convergeTag;
        }

        public void setConvergeTag(String convergeTag) {
            this.convergeTag = convergeTag;
        }

        public void setChangedPathToMap(String uri, Map<String, String> routerMap) {
            String originalPath = AntMatchUtil.path(uri).substring(AntMatchUtil.path(getServicePrefix()).length());
            String changedPath =
                AntMatchUtil.replacePathWithinPattern(getTransformPath(), originalPath, getPatternPath());
            routerMap.put(ROUTER_PATH, changedPath);
            routerMap.put(CONVERGE_TAG, StringUtils.isBlank(getConvergeTag()) ? StringUtils.EMPTY : getConvergeTag());
        }

        public void setRouterToMap(SpringCloudDiscovery springCloudDiscovery, Map<String, String> routerMap) {
            ServiceRouterDTO routerDTO = AbstractPlugin.getRouterByServiceId(serviceId);
            if (RouteTypeEnum.DirectRoute.getCode().equalsIgnoreCase(routerDTO.getRouterType())) {
                DirectRoutingDefinition definition =
                    JsonUtils.json2Definition(routerDTO.getRouterParam(), DirectRoutingDefinition.class);
                if (definition == null) {
                    return;
                }
                routerMap.put(HOST_AND_PORT, ProxyUtils.parseHostAndPort(definition.getTargetHostPort()));
                if (YesOrNoEnum.YES.getCode().equals(definition.getEnableSSL())) {
                    routerMap.put(X_TESLA_ENABLE_SSL, definition.getEnableSSL());
                    if (StringUtils.isNotBlank(definition.getSelfSignCrtFileId())) {
                        routerMap.put(X_TESLA_SELF_SIGN_CRT, definition.getSelfSignCrtFileId());
                    }
                }
            } else if (RouteTypeEnum.SpringCloud.getCode().equalsIgnoreCase(routerDTO.getRouterType())) {
                SpringCloudRoutingDefinition definition =
                    JsonUtils.json2Definition(routerDTO.getRouterParam(), SpringCloudRoutingDefinition.class);
                if (definition == null) {
                    return;
                }
                Map<String, String> groupVersionMap = Maps.newHashMap();
                if (StringUtils.isNotBlank(definition.getGroup())) {
                    groupVersionMap.put(DiscoveryClientWrapper.EUREKA_METADATA_GROUP, definition.getGroup());
                }
                if (StringUtils.isNotBlank(definition.getVersion())) {
                    groupVersionMap.put(DiscoveryClientWrapper.EUREKA_METADATA_VERSION, definition.getVersion());
                }
                InstanceInfo instanceInfo =
                    springCloudDiscovery.nextServer(definition.getServiceName(), groupVersionMap);
                routerMap.put(HOST_AND_PORT,
                    String.format("%s:%s", instanceInfo.getHostName(), instanceInfo.getPort()));
            }
        }

    }

}
