package io.github.tesla.filter;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.hazelcast.core.HazelcastInstance;

import io.github.tesla.common.dto.ServiceRouterDTO;
import io.github.tesla.common.service.GatewayApiTextService;
import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.common.definition.CacheConstant;
import io.github.tesla.filter.support.springcloud.SpringCloudDiscovery;

/**
 * @author: zhangzhiping
 * @date: 2018/11/29 15:25
 * @description:
 */
public class AbstractPlugin {

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractPlugin.class);

    private static Map<String, byte[]> fileLocalCacheMap = Maps.newHashMap();

    private static Map<String, ServiceRouterDTO> routerLocalCache = Maps.newHashMap();

    private static HazelcastInstance hazelcastInstance;

    // 应用的接入配置缓存
    public static Map<String, Map<String, String>> appKeyLocalDefinitionMap = Maps.newHashMap();

    public static void clearLocalCache() {
        fileLocalCacheMap.clear();
        routerLocalCache.clear();
    }

    public static byte[] getFileBytesByKey(String key) {
        try {
            CacheConstant.READ_WRITE_LOCK.readLock().lock();
            if (fileLocalCacheMap.get(key) == null) {
                fileLocalCacheMap.put(key,
                    (byte[])getHazelcastInstance().getMap(CacheConstant.FILE_CACHE_MAP).get(key));
            }
            return fileLocalCacheMap.get(key);
        } finally {
            CacheConstant.READ_WRITE_LOCK.readLock().unlock();
        }
    }

    public static HazelcastInstance getHazelcastInstance() {
        if (AbstractPlugin.hazelcastInstance == null) {
            AbstractPlugin.hazelcastInstance = SpringContextHolder.getBean(HazelcastInstance.class);
        }
        return hazelcastInstance;
    }

    public static ServiceRouterDTO getRouterByServiceId(String serviceId) {
        try {
            CacheConstant.READ_WRITE_LOCK.readLock().lock();
            if (routerLocalCache.get(serviceId) == null) {
                routerLocalCache.put(serviceId, SpringContextHolder.getBean(GatewayApiTextService.class)
                    .loadGatewayServiceByServiceId(serviceId).getRouterDTO());
            }
            return routerLocalCache.get(serviceId);
        } finally {
            CacheConstant.READ_WRITE_LOCK.readLock().unlock();
        }
    }

    public static void setHazelcastInstance(HazelcastInstance hz) {
        hazelcastInstance = hz;
    }

    private SpringCloudDiscovery springCloudDiscovery;

    public Map<String, String> getAppKeyMap(String appKey) {
        return appKeyLocalDefinitionMap.get(appKey);
    }

    public SpringCloudDiscovery getSpringCloudDiscovery() {
        return springCloudDiscovery;
    }

    public void setSpringCloudDiscovery(SpringCloudDiscovery springCloudDiscovery) {
        this.springCloudDiscovery = springCloudDiscovery;
    }
}
