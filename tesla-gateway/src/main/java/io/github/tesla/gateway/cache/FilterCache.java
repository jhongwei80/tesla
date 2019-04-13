package io.github.tesla.gateway.cache;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.impl.MapListenerAdapter;

import io.github.tesla.common.domain.GatewayFileDO;
import io.github.tesla.common.domain.GatewayWafDO;
import io.github.tesla.common.dto.*;
import io.github.tesla.common.service.GatewayApiTextService;
import io.github.tesla.common.service.GatewayCacheRefreshService;
import io.github.tesla.common.service.GatewayFileService;
import io.github.tesla.common.service.GatewayWafService;
import io.github.tesla.filter.AbstractPlugin;
import io.github.tesla.filter.common.definition.CacheConstant;
import io.github.tesla.filter.support.plugins.*;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.ClassUtils;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.gateway.excutor.*;

/**
 * @author: zhangzhiping
 * @date: 2018/11/23 14:28
 * @description:缓存刷新类
 */
@Component
@EnableScheduling
public class FilterCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractScheduleCache.class);

    @Autowired
    private GatewayWafService gatewayWafService;
    @Autowired
    private GatewayApiTextService gatewayApiTextService;
    @Autowired
    private GatewayFileService gatewayFileService;
    @Autowired
    private GatewayCacheRefreshService cacheRefreshService;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    // WAF Request请求配置缓存
    private List<WafRequestPluginExecutor> wafRequestCacheList;

    // WAF Response请求配置缓存
    private List<WafResponsePluginExecutor> wafResponseCacheList;

    // 服务级别配置缓存
    private List<ServiceExecutor> apiCacheList;

    // 应用的接入配置缓存
    private Map<String, Map<String, String>> appKeyDefinitionMap;

    // 二进制文件缓存
    private Map<String, byte[]> fileCacheMap;

    // 缓存刷新时间
    private Map<String, Timestamp> cacheRefreshTime;

    private ILock distributeLock;

    // WAF Request请求配置本地缓存
    private List<WafRequestPluginExecutor> wafRequestLocalCacheList = Lists.newArrayList();

    // WAF Response请求配置本地缓存
    private List<WafResponsePluginExecutor> wafResponseLocalCacheList = Lists.newArrayList();

    // 服务级别配置本地缓存
    private List<ServiceExecutor> apiLocalCacheList = Lists.newArrayList();

    private void buildAndAddApiCaches(List<ServiceRequestPluginExecutor> requestFilterCaches,
        List<ServiceResponsePluginExecutor> reponseFilterCaches, CommonPluginDTO servicePluginDTO,
        Set<String> ignoreClassSet) throws Exception {
        RequestPluginMetadata requestPluginEnum =
            ServiceRequestPluginMetadata.getMetadataByType(servicePluginDTO.getPluginType());
        CommonPluginExecutor cache = initBaseFilterCache(servicePluginDTO);
        if (requestPluginEnum != null) {
            cache = buildAndAddRequestFilter(requestFilterCaches, ignoreClassSet, requestPluginEnum, cache);
            if (cache == null)
                return;
        }
        ResponsePluginMetadata responsePluginEnum =
            ServiceResponsePluginMetadata.getMetadataByType(servicePluginDTO.getPluginType());
        if (responsePluginEnum != null) {
            cache = buildAndAddResponseFilter(reponseFilterCaches, ignoreClassSet, cache, responsePluginEnum);
            if (cache == null)
                return;
        }
    }

    private void buildAndAddEndpointCaches(List<ServiceRequestPluginExecutor> requestFilterCaches,
        List<ServiceResponsePluginExecutor> reponseFilterCaches, CommonPluginDTO endpointPluginDTO,
        Set<String> ignoreClassSet) throws Exception {
        RequestPluginMetadata requestPluginEnum =
            EndpointRequestPluginMetadata.getMetadataByType(endpointPluginDTO.getPluginType());
        CommonPluginExecutor cache = initBaseFilterCache(endpointPluginDTO);
        if (requestPluginEnum != null) {
            cache = buildAndAddRequestFilter(requestFilterCaches, ignoreClassSet, requestPluginEnum, cache);
            if (cache == null)
                return;
        }
        ResponsePluginMetadata responsePluginEnum =
            EndpointResponsePluginMetadata.getMetadataByType(endpointPluginDTO.getPluginType());
        if (responsePluginEnum != null) {
            cache = buildAndAddResponseFilter(reponseFilterCaches, ignoreClassSet, cache, responsePluginEnum);
            if (cache == null)
                return;
        }
    }

    private CommonPluginExecutor buildAndAddRequestFilter(List<ServiceRequestPluginExecutor> requestFilterCaches,
        Set<String> ignoreClassSet, RequestPluginMetadata requestPluginEnum, CommonPluginExecutor cache)
        throws Exception {
        if (requestPluginEnum.getIgnoreClassType() != null) {
            ignoreClassSet.add(requestPluginEnum.getIgnoreClassType());
            return null;
        }
        cache = new ServiceRequestPluginExecutor(cache);
        cache.setOrder(requestPluginEnum.getFilterOrder());
        ((ServiceRequestPluginExecutor)cache).setRequestPluginEnum(requestPluginEnum);
        requestFilterCaches.add(((ServiceRequestPluginExecutor)cache));
        return cache;
    }

    private CommonPluginExecutor buildAndAddResponseFilter(List<ServiceResponsePluginExecutor> reponseFilterCaches,
        Set<String> ignoreClassSet, CommonPluginExecutor cache, ResponsePluginMetadata responsePluginEnum)
        throws Exception {
        if (responsePluginEnum.getIgnoreClassType() != null) {
            ignoreClassSet.add(responsePluginEnum.getIgnoreClassType());
            return null;
        }
        cache = new ServiceResponsePluginExecutor(cache);
        cache.setOrder(responsePluginEnum.getFilterOrder());
        ((ServiceResponsePluginExecutor)cache).setResponsePluginEnum(responsePluginEnum);
        reponseFilterCaches.add(((ServiceResponsePluginExecutor)cache));
        return cache;
    }

    /*** Help method ***/
    private void buildEndpointCache(List<EndpointExecutor> endpointCaches, ServiceDTO serviceDTO) throws Exception {
        for (EndpointDTO endpointDTO : serviceDTO.getEndpointDTOList()) {
            EndpointExecutor endpointCache = new EndpointExecutor();
            endpointCache.setEndPointMethod(endpointDTO.getEndpointMethod());
            endpointCache.setEndPointPath(endpointDTO.getEndpointUrl());
            List<ServiceRequestPluginExecutor> requestFilterCaches = Lists.newArrayList();
            List<ServiceResponsePluginExecutor> reponseFilterCaches = Lists.newArrayList();
            Set<String> ignoreClassSet = Sets.newHashSet();
            if (endpointDTO.getPluginDTOList() != null && endpointDTO.getPluginDTOList().size() > 0) {
                for (EndpointPluginDTO endpointPluginDTO : endpointDTO.getPluginDTOList()) {
                    buildAndAddEndpointCaches(requestFilterCaches, reponseFilterCaches, endpointPluginDTO,
                        ignoreClassSet);
                }
            }
            if (serviceDTO.getPluginDTOList() != null && serviceDTO.getPluginDTOList().size() > 0) {
                for (ServicePluginDTO apiPluginDTO : serviceDTO.getPluginDTOList()) {
                    buildAndAddApiCaches(requestFilterCaches, reponseFilterCaches, apiPluginDTO, ignoreClassSet);
                }
            }
            if (ignoreClassSet.size() > 0) {
                requestFilterCaches = requestFilterCaches.stream()
                    .filter((cache) -> !ignoreClassSet.contains(cache.getFilterType())).collect(Collectors.toList());
                reponseFilterCaches = reponseFilterCaches.stream()
                    .filter((cache) -> !ignoreClassSet.contains(cache.getFilterType())).collect(Collectors.toList());
            }
            Collections.sort(requestFilterCaches);
            Collections.sort(reponseFilterCaches);
            endpointCache.setRequestFiltersList(requestFilterCaches);
            endpointCache.setResponseFiltersList(reponseFilterCaches);
            endpointCaches.add(endpointCache);
        }
    }

    @Scheduled(cron = "35 */5 * * * ?")
    protected void checkLocalCache() {
        boolean needRefreshLocalCache = false;
        try {
            LOGGER.info("begin check local cache ");
            CacheConstant.READ_WRITE_LOCK.readLock().lock();
            if (wafRequestLocalCacheList.size() != wafRequestCacheList.size()) {
                needRefreshLocalCache = true;
            }
            if (wafResponseLocalCacheList.size() != wafResponseCacheList.size()) {
                needRefreshLocalCache = true;
            }
            if (apiLocalCacheList.size() != apiCacheList.size()) {
                needRefreshLocalCache = true;
            }
            if (AbstractPlugin.appKeyLocalDefinitionMap.size() != appKeyDefinitionMap.size()) {
                needRefreshLocalCache = true;
            }
        } finally {
            CacheConstant.READ_WRITE_LOCK.readLock().unlock();
        }
        if (needRefreshLocalCache) {
            refreshLocalCache();
        }
    }

    @Scheduled(cron = "0 */1 * * * ?")
    protected void doCache() {
        try {
            if (cacheRefreshService.isRefreshCache(cacheRefreshTime)) {
                refreshClusterCache();
            }
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void doCacheAppKeyCache() throws Exception {
        List<AppKeyDTO> appKeyDTOS = gatewayApiTextService.loadEnabledAppKey();
        if (CollectionUtils.isEmpty(appKeyDTOS)) {
            return;
        }
        Map<String, Map<String, String>> tmpAppKeyDefinitionMap = Maps.newConcurrentMap();
        for (AppKeyDTO appKeyDTO : appKeyDTOS) {
            Map<String, String> pluginParamMap = Maps.newConcurrentMap();
            if (CollectionUtils.isEmpty(appKeyDTO.getAppKeyPluginDTOS())) {
            } else {
                for (AppKeyPluginDTO appPluginDTO : appKeyDTO.getAppKeyPluginDTOS()) {
                    pluginParamMap.put(appPluginDTO.getPluginType(), appPluginDTO.getPluginParam());
                }
            }
            tmpAppKeyDefinitionMap.put(appKeyDTO.getAppKey(), pluginParamMap);
        }
        appKeyDefinitionMap.clear();
        appKeyDefinitionMap.putAll(tmpAppKeyDefinitionMap);
    }

    private void doCacheFileCache() {
        List<GatewayFileDO> fileDOList = gatewayFileService.loadEnabledFile();
        if (CollectionUtils.isEmpty(fileDOList)) {
            return;
        }
        fileCacheMap.clear();
        for (GatewayFileDO fileDO : fileDOList) {
            fileCacheMap.put(fileDO.getFileId(), fileDO.getFileBlob());
        }
    }

    private void doCacheServiceCache() throws Exception {
        List<ServiceDTO> serviceDTOS = gatewayApiTextService.loadEnabledService();
        if (serviceDTOS == null || serviceDTOS.size() == 0) {
            return;
        }
        List<ServiceExecutor> tmpApiCacheList = Lists.newArrayList();
        for (ServiceDTO serviceDTO : serviceDTOS) {
            ServiceExecutor serviceDefinition = new ServiceExecutor();
            List<EndpointExecutor> endpointCaches = Lists.newArrayList();
            serviceDefinition.setServicePrefix(serviceDTO.getServicePrefix());
            ServiceRouterExecutor routerCache = new ServiceRouterExecutor();
            routerCache.setRouteType(serviceDTO.getRouterDTO().getRouterType());
            routerCache.setParamJson(serviceDTO.getRouterDTO().getRouterParam());
            serviceDefinition.setRouterCache(routerCache);
            if (CollectionUtils.isEmpty(serviceDTO.getEndpointDTOList())) {
                continue;
            }
            try {
                buildEndpointCache(endpointCaches, serviceDTO);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            }
            Collections.sort(endpointCaches);
            serviceDefinition.setEndPointDefinitionList(endpointCaches);
            tmpApiCacheList.add(serviceDefinition);
        }
        Collections.sort(tmpApiCacheList);
        apiCacheList.clear();
        apiCacheList.addAll(tmpApiCacheList);
    }

    private void doCacheWafCache() throws Exception {
        List<GatewayWafDO> wafDOS = gatewayWafService.loadEnabledWaf();
        if (CollectionUtils.isEmpty(wafDOS)) {
            return;
        }
        List<WafRequestPluginExecutor> tmpWafRequestCacheList = Lists.newArrayList();
        List<WafResponsePluginExecutor> tmpWafResponseCacheList = Lists.newArrayList();
        Set<String> ignoreClassSet = Sets.newHashSet();
        for (GatewayWafDO waf : wafDOS) {
            try {
                WafRequestPluginMetadata requestEnum = WafRequestPluginMetadata.getMetadataByType(waf.getWafType());
                if (requestEnum != null) {
                    WafRequestPluginExecutor cache = new WafRequestPluginExecutor();
                    cache.setFilterName(waf.getWafName());
                    cache.setFilterType(waf.getWafType());
                    cache.setParamJson(waf.getPluginParam());
                    if (requestEnum.getIgnoreClassType() != null) {
                        ignoreClassSet.add(requestEnum.getIgnoreClassType());
                        continue;
                    }
                    if (requestEnum.getFilterClass() != null) {
                        cache.setOrder(requestEnum.getFilterOrder());
                    }
                    cache.setRequestPluginEnum(requestEnum);
                    tmpWafRequestCacheList.add(cache);
                }
                WafResponsePluginMetadata responseEnum = WafResponsePluginMetadata.getMetadataByType(waf.getWafType());
                if (responseEnum != null) {
                    WafResponsePluginExecutor cache = new WafResponsePluginExecutor();
                    cache.setFilterName(waf.getWafName());
                    cache.setFilterType(waf.getWafType());
                    cache.setParamJson(waf.getPluginParam());
                    if (responseEnum.getIgnoreClassType() != null) {
                        ignoreClassSet.add(responseEnum.getIgnoreClassType());
                        continue;
                    }
                    if (responseEnum.getFilterClass() != null) {
                        cache.setOrder(responseEnum.getFilterOrder());
                    }
                    cache.setResponsePluginEnum(responseEnum);
                    tmpWafResponseCacheList.add(cache);
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
                throw e;
            }

        }
        if (ignoreClassSet.size() > 0) {
            tmpWafRequestCacheList = tmpWafRequestCacheList.stream()
                .filter((cache) -> !ignoreClassSet.contains(cache.getFilterType())).collect(Collectors.toList());
            tmpWafResponseCacheList = tmpWafResponseCacheList.stream()
                .filter((cache) -> !ignoreClassSet.contains(cache.getFilterType())).collect(Collectors.toList());
        }
        Collections.sort(tmpWafRequestCacheList);
        Collections.sort(tmpWafResponseCacheList);
        wafRequestCacheList.clear();
        wafRequestCacheList.addAll(tmpWafRequestCacheList);
        wafResponseCacheList.clear();
        wafResponseCacheList.addAll(tmpWafResponseCacheList);
    }

    @PostConstruct
    public void init() {
        distributeLock = hazelcastInstance.getLock(CacheConstant.CACHE_REFRESH_LOCK);
        wafRequestCacheList = hazelcastInstance.getList(CacheConstant.WAF_REQUEST_CACHE_LIST);
        wafResponseCacheList = hazelcastInstance.getList(CacheConstant.WAF_RESPONSE_CACHE_LIST);
        apiCacheList = hazelcastInstance.getList(CacheConstant.API_CACHE_LIST);
        fileCacheMap = hazelcastInstance.getMap(CacheConstant.FILE_CACHE_MAP);
        cacheRefreshTime = hazelcastInstance.getMap(CacheConstant.CACHE_REFRESH_TIME);
        appKeyDefinitionMap = hazelcastInstance.getMap(CacheConstant.APP_KEY_CACHE_MAP);
        hazelcastInstance.getMap(CacheConstant.CACHE_REFRESH_TIME)
            .addEntryListener(new MapListenerAdapter<String, Timestamp>() {
                @Override
                public void onEntryEvent(EntryEvent<String, Timestamp> event) {
                    LOGGER.info(
                        "hazelcast value refreshTime changed , refresh Local cache , key : {} ,value: {} , eventType : {}",
                        event.getKey(), event.getValue(), event.getEventType());
                    refreshLocalCache();
                }

                @Override
                public void onMapEvent(MapEvent event) {
                    LOGGER.info("hazelcast map refreshTime changed , refresh Local cache , eventType : {}",
                        event.getEventType());
                    refreshLocalCache();
                }

            }, true);
        doCache();
        refreshLocalCache();
    }

    private CommonPluginExecutor initBaseFilterCache(CommonPluginDTO apiPluginDTO) {
        CommonPluginExecutor cache = new CommonPluginExecutor();
        cache.setFilterName(apiPluginDTO.getPluginName());
        cache.setFilterType(apiPluginDTO.getPluginType());
        cache.setParamJson(apiPluginDTO.getPluginParam());
        return cache;
    }

    public byte[] loadFileBytes(String fileId) {
        return fileCacheMap.get(fileId);
    }

    public ServiceExecutor loadServiceCache(String uri) {
        for (ServiceExecutor serviceCache : apiLocalCacheList) {
            if (AntMatchUtil.matchPrefix(serviceCache.getServicePrefix(), uri)) {
                return serviceCache;
            }
        }
        return null;
    }

    public List<WafRequestPluginExecutor> loadWafRequestPlugins() {
        return wafRequestLocalCacheList;
    }

    public List<WafResponsePluginExecutor> loadWafResonsePlugins() {
        return wafResponseLocalCacheList;
    }

    private void refreshClusterCache() {
        try {
            // 30s拿不到锁，放弃
            // 60s锁自动放掉
            if (distributeLock.tryLock(30, TimeUnit.SECONDS, 60, TimeUnit.SECONDS)) {
                try {
                    CacheConstant.READ_WRITE_LOCK.writeLock().lock();
                    if (!cacheRefreshService.isRefreshCache(cacheRefreshTime)) {
                        LOGGER.info("do not need refresh cache");
                        return;
                    }
                    doCacheWafCache();
                    doCacheServiceCache();
                    doCacheFileCache();
                    doCacheAppKeyCache();
                    LOGGER.info("wafRequestCache:" + JsonUtils.serializeToJson(wafRequestCacheList));
                    LOGGER.info("wafResponseCache:" + JsonUtils.serializeToJson(wafResponseCacheList));
                    LOGGER.info("apiCache:" + JsonUtils.serializeToJson(apiCacheList));
                    LOGGER.info("fileCache:" + JsonUtils.serializeToJson(fileCacheMap.keySet()));
                    LOGGER.info("appKeyCache:" + JsonUtils.serializeToJson(appKeyDefinitionMap));
                    cacheRefreshService.updateRefreshTime(cacheRefreshTime);
                } catch (Exception e) {
                    LOGGER.error(e.getMessage(), e);
                    throw new RuntimeException(e);
                } finally {
                    CacheConstant.READ_WRITE_LOCK.writeLock().unlock();
                    distributeLock.unlock();
                }
            } else {
                LOGGER.info("The instance did not get hazelcast distributeLock ");
                throw new RuntimeException();
            }
        } catch (InterruptedException e) {
            LOGGER.warn(" 30 seconds cant get lock from hazelcast,will retry in next 1 min");
            throw new RuntimeException(e);
        }
    }

    public void refreshLocalCache() {
        try {
            LOGGER.info("begin refresh local cache ");
            CacheConstant.READ_WRITE_LOCK.writeLock().lock();
            ClassUtils.cleanCacheBean();

            wafRequestLocalCacheList = Lists.newArrayList(wafRequestCacheList);

            wafResponseLocalCacheList = Lists.newArrayList(wafResponseCacheList);

            apiLocalCacheList = Lists.newArrayList(apiCacheList);

            AbstractPlugin.appKeyLocalDefinitionMap = Maps.newHashMap(appKeyDefinitionMap);

            AbstractPlugin.clearLocalCache();

            LOGGER.info("wafRequestLocalCache:" + JsonUtils.serializeToJson(wafRequestLocalCacheList));
            LOGGER.info("wafResponseLocalCache:" + JsonUtils.serializeToJson(wafResponseLocalCacheList));
            LOGGER.info("apiLocalCache:" + JsonUtils.serializeToJson(apiLocalCacheList));
            LOGGER.info("appKeyLocalCache:" + JsonUtils.serializeToJson(AbstractPlugin.appKeyLocalDefinitionMap));
        } finally {
            CacheConstant.READ_WRITE_LOCK.writeLock().unlock();
        }
    }

}
