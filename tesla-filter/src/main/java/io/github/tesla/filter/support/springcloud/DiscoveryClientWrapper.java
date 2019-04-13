/*
 * Copyright (c) 2018 DISID CORPORATION S.L.
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

package io.github.tesla.filter.support.springcloud;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ReflectionUtils;

import com.google.common.collect.Lists;
import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryClient;
import com.netflix.discovery.EurekaClientConfig;

/**
 * ClassName:DiscoveryClientWrapper <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2018年7月5日 下午12:55:12 <br/>
 * 
 * @author liushiming
 * @version
 * @since JDK 10
 * @see
 */
public class DiscoveryClientWrapper extends DiscoveryClient {

    public static final String EUREKA_METADATA_GROUP = "GROUP";
    public static final String EUREKA_METADATA_VERSION = "VERSION";

    private ThreadLocal<List<Map<String, String>>> groupVersionMapList = new ThreadLocal<>();

    public DiscoveryClientWrapper(ApplicationInfoManager applicationInfoManager, EurekaClientConfig config) {
        super(applicationInfoManager, config);
    }

    /* private Map<String, String> groupVersionMap;*/

    public void clearGroupVersionMap() {
        groupVersionMapList.remove();
    }

    private List<InstanceInfo> filterInstance(List<InstanceInfo> instanceList) {
        try {
            if (CollectionUtils.isEmpty(groupVersionMapList.get())) {
                return instanceList;
            } else {
                List<InstanceInfo> filteredInsantceList = instanceList;
                for (Map<String, String> groupVersionMap : groupVersionMapList.get()) {
                    filteredInsantceList = filterInstanceInner(filteredInsantceList, groupVersionMap);
                }
                return filteredInsantceList;
            }
        } finally {
            // Notice:这里用完了一定要清除掉，不然会污染其他的ServiceId
            clearGroupVersionMap();
        }
    }

    private List<InstanceInfo> filterInstanceInner(List<InstanceInfo> instanceList,
        Map<String, String> groupVersionMap) {
        final List<InstanceInfo> filteredInsantce = Lists.newArrayList();
        boolean match = MapUtils.getBooleanValue(groupVersionMap, "match", true);
        for (InstanceInfo insaceInfo : instanceList) {
            final Map<String, String> instanceMeta = insaceInfo.getMetadata();
            if (groupVersionMap != null && !groupVersionMap.isEmpty()) {
                groupVersionMap.remove("match");
                boolean equals = true;
                for (String meteDataKey : groupVersionMap.keySet()) {
                    if (StringUtils.isNotBlank(MapUtils.getString(groupVersionMap, meteDataKey))
                        && !StringUtils.equals(MapUtils.getString(groupVersionMap, meteDataKey),
                            MapUtils.getString(instanceMeta, meteDataKey))) {
                        equals = false;
                    }
                }
                if (equals) {
                    filteredInsantce.add(insaceInfo);
                }
            } else {
                filteredInsantce.add(insaceInfo);
            }
        }
        if (!match) {
            return instanceList.stream().filter(instanceInfo -> !filteredInsantce.contains(instanceInfo))
                .collect(Collectors.toList());
        }
        return filteredInsantce;
    }

    @Override
    public List<InstanceInfo> getInstancesByVipAddress(String vipAddress, boolean secure) {
        List<InstanceInfo> instanceInfos = super.getInstancesByVipAddress(vipAddress, secure);
        return this.filterInstance(instanceInfos);
    }

    @Override
    public List<InstanceInfo> getInstancesByVipAddress(String vipAddress, boolean secure, String region) {
        List<InstanceInfo> instanceInfos = super.getInstancesByVipAddress(vipAddress, secure, region);
        return this.filterInstance(instanceInfos);
    }

    @Override
    public List<InstanceInfo> getInstancesByVipAddressAndAppName(String vipAddress, String appName, boolean secure) {
        List<InstanceInfo> instanceInfos = super.getInstancesByVipAddressAndAppName(vipAddress, appName, secure);
        return this.filterInstance(instanceInfos);
    }

    @Override
    public InstanceInfo getNextServerFromEureka(String virtualHostname, boolean secure) {
        try {
            InstanceInfo instanceInfo = super.getNextServerFromEureka(virtualHostname, secure);
            return instanceInfo;
        } catch (RuntimeException e) {
            Method refreshRegistry = ReflectionUtils.findMethod(this.getClass(), "refreshRegistry");
            ReflectionUtils.makeAccessible(refreshRegistry);
            ReflectionUtils.invokeMethod(refreshRegistry, this);
            throw e;
        }
    }

    public void setGroupVersion(List<Map<String, String>> pair) {
        if (CollectionUtils.isEmpty(pair)) {
            return;
        }
        if (groupVersionMapList.get() == null) {
            groupVersionMapList.set(Lists.newArrayList());
        }
        this.groupVersionMapList.get().addAll(pair);
    }

    public void setGroupVersion(Map<String, String> groupVersion) {
        if (groupVersionMapList.get() == null) {
            groupVersionMapList.set(Lists.newArrayList());
        }
        this.groupVersionMapList.get().add(groupVersion);
    }

}
