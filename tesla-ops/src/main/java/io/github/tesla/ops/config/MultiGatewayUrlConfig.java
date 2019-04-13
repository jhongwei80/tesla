package io.github.tesla.ops.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;

import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.ops.common.MultiGatewayUrlSwitcher;

@Configuration
public class MultiGatewayUrlConfig {

    @Value("${tesla.gateway.url}")
    private String gatewayUrl;

    @Bean("multiGatewaySwitcher")
    public MultiGatewayUrlSwitcher getMultiGatewaySwitcher() {
        MultiGatewayUrlSwitcher multiGatewayUrlSwitcher = new MultiGatewayUrlSwitcher();
        initMultiGateway(multiGatewayUrlSwitcher);
        return multiGatewayUrlSwitcher;
    }

    public void initMultiGateway(MultiGatewayUrlSwitcher multiGatewayUrlSwitcher) {
        if (JsonUtils.isJson(gatewayUrl)) {
            initMultiGatewayWithMapConfig(multiGatewayUrlSwitcher);
        } else {
            initMultiGatewayWithStringConfig(multiGatewayUrlSwitcher);
        }
    }

    public void initMultiGatewayWithMapConfig(MultiGatewayUrlSwitcher multiGatewayUrlSwitcher) {
        Map<String, String> multiGatewayUrlMap = Maps.newConcurrentMap();
        Map<String, String> multiGatewayUrlConfigMap = JsonUtils.fromJson(gatewayUrl, HashMap.class);

        multiGatewayUrlConfigMap.entrySet().forEach(entry -> {
            multiGatewayUrlMap.put(entry.getKey(), entry.getValue());
        });
        multiGatewayUrlSwitcher.setMultiGatewayUrlMap(multiGatewayUrlMap);
    }

    public void initMultiGatewayWithStringConfig(MultiGatewayUrlSwitcher multiGatewayUrlSwitcher) {
        Map<String, String> multiGatewayUrlMap = Maps.newConcurrentMap();
        multiGatewayUrlMap.put("local", gatewayUrl);
        multiGatewayUrlSwitcher.setMultiGatewayUrlMap(multiGatewayUrlMap);
    }

}
