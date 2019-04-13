/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.gateway.protocol.springcloud;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.netflix.appinfo.InstanceInfo;

import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.support.springcloud.DiscoveryClientWrapper;
import io.github.tesla.filter.support.springcloud.SpringCloudDiscovery;
import io.github.tesla.gateway.cache.GrayRuesCache;

/**
 * @author liushiming
 * @version DynamicSpringCloudClient.java, v 0.0.1 2018年5月4日 上午11:53:15 liushiming
 */
public class DynamicSpringCloudClient {

    @Autowired
    private DiscoveryClientWrapper discoveryClientWrapper;

    private final int httpPort;

    public DynamicSpringCloudClient(int httpPort) {
        this.httpPort = httpPort;
    }

    public SpringCloudDiscovery getSpringCloudDiscovery() {
        return new SpringCloudDiscovery(discoveryClientWrapper, httpPort);
    }

    public String loadBalanceCall(String serviceId, String group, String version,
        NettyHttpServletRequest servletRequest) {
        if (StringUtils.isNotBlank(group) && StringUtils.isNotBlank(version)) {
            Map<String, String> groupVersionMap = Maps.newHashMap();
            groupVersionMap.put(DiscoveryClientWrapper.EUREKA_METADATA_VERSION, version);
            groupVersionMap.put(DiscoveryClientWrapper.EUREKA_METADATA_GROUP, group);
            discoveryClientWrapper.setGroupVersion(groupVersionMap);
        }
        List<Map<String, String>> pair = GrayRuesCache.getTargetApp(serviceId, servletRequest);
        if (!CollectionUtils.isEmpty(pair)) {
            discoveryClientWrapper.clearGroupVersionMap();
            discoveryClientWrapper.setGroupVersion(pair);
        }
        InstanceInfo instanceInfo = this.nextServer(serviceId);
        return instanceInfo.getHostName() + ":" + instanceInfo.getPort();
    }

    private InstanceInfo nextServer(String serviceId) {
        return discoveryClientWrapper.getNextServerFromEureka(serviceId, false);
    }

}
