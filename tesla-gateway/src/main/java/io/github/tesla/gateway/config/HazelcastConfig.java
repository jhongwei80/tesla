package io.github.tesla.gateway.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.discovery.DiscoveryNode;
import com.hazelcast.spi.discovery.SimpleDiscoveryNode;
import com.hazelcast.spi.discovery.integration.DiscoveryService;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceProvider;
import com.hazelcast.spi.discovery.integration.DiscoveryServiceSettings;
import com.netflix.appinfo.InstanceInfo;

import io.github.tesla.filter.common.definition.CacheConstant;
import io.github.tesla.filter.support.springcloud.DiscoveryClientWrapper;

@Configuration
public class HazelcastConfig {

    private static final Logger logger = LoggerFactory.getLogger(HazelcastConfig.class);

    @Value("${spring.application.name}")
    private String clusterName;
    @Value("${hazelcast.version:1.0.0}")
    private String hazelcastVersion;

    @Bean
    public Config config(DiscoveryServiceProvider discoveryServiceProvider) {
        Config config = new Config();
        config.getGroupConfig().setName(clusterName.toUpperCase());
        config.setProperty("hazelcast.discovery.enabled", Boolean.TRUE.toString());
        JoinConfig joinConfig = config.getNetworkConfig().getJoin();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getDiscoveryConfig().setDiscoveryServiceProvider(discoveryServiceProvider);
        // init hazelcast cache
        config.getMapConfigs().put(CacheConstant.CACHE_REFRESH_TIME,
            new MapConfig(CacheConstant.CACHE_REFRESH_TIME).setBackupCount(1));
        config.getListConfigs().put(CacheConstant.API_CACHE_LIST,
            new ListConfig(CacheConstant.API_CACHE_LIST).setBackupCount(1));
        config.getListConfigs().put(CacheConstant.WAF_REQUEST_CACHE_LIST,
            new ListConfig(CacheConstant.WAF_REQUEST_CACHE_LIST).setBackupCount(1));
        config.getListConfigs().put(CacheConstant.WAF_RESPONSE_CACHE_LIST,
            new ListConfig(CacheConstant.WAF_RESPONSE_CACHE_LIST).setBackupCount(1));
        config.getMapConfigs().put(CacheConstant.APP_KEY_CACHE_MAP,
            new MapConfig(CacheConstant.APP_KEY_CACHE_MAP).setBackupCount(1));
        config.getMapConfigs().put(CacheConstant.FILE_CACHE_MAP,
            new MapConfig(CacheConstant.FILE_CACHE_MAP).setBackupCount(1));

        return config;
    }

    @Bean
    public DiscoveryServiceProvider discoveryServiceProvider(DiscoveryClientWrapper discoveryClient) {
        return new DiscoveryServiceProvider() {

            @Override
            public DiscoveryService newDiscoveryService(DiscoveryServiceSettings settings) {
                return new DiscoveryService() {

                    @Override
                    public void destroy() {}

                    @Override
                    public Map<String, Object> discoverLocalMetadata() {
                        return Collections.emptyMap();
                    }

                    @Override
                    public Iterable<DiscoveryNode> discoverNodes() {
                        List<DiscoveryNode> nodes = new ArrayList<>();
                        discoveryClient.getInstancesByVipAddress(clusterName, false)
                            .forEach((InstanceInfo serviceInstance) -> {
                                try {
                                    String host = serviceInstance.getMetadata().get("hazelcast.host");
                                    String port = serviceInstance.getMetadata().get("hazelcast.port");
                                    String version = serviceInstance.getMetadata().get("hazelcast.version");
                                    if (host != null && port != null && StringUtils.equals(version, hazelcastVersion)) {
                                        Address address = new Address(host, Integer.parseInt(port));
                                        DiscoveryNode discoveryNode = new SimpleDiscoveryNode(address);
                                        nodes.add(discoveryNode);
                                    }
                                } catch (Exception e) {
                                    logger.error("discoverNodes()", e);
                                }
                            });
                        return nodes;
                    }

                    @Override
                    public void start() {}

                };
            }

        };
    }

}
