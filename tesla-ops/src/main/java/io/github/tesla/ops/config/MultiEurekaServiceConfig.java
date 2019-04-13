package io.github.tesla.ops.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;

import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.ops.common.MultiEurekaServiceSwitcher;

@Configuration
public class MultiEurekaServiceConfig {

    @Value("${tesla.eureka.url}")
    private String eurekaUrl;

    @Bean("multiEurekaSwitcher")
    public MultiEurekaServiceSwitcher getMultiEurekaSwitcher() {
        MultiEurekaServiceSwitcher multiEurekaServiceSwitcher = new MultiEurekaServiceSwitcher();
        initMultiEureka(multiEurekaServiceSwitcher);
        return multiEurekaServiceSwitcher;
    }

    public void initMultiEureka(MultiEurekaServiceSwitcher multiEurekaServiceSwitcher) {
        if (JsonUtils.isJson(eurekaUrl)) {
            initMultiEurekaWithMapConfig(multiEurekaServiceSwitcher);
        } else {
            initMultiEurekaWithStringConfig(multiEurekaServiceSwitcher);
        }
    }

    public void initMultiEurekaWithMapConfig(MultiEurekaServiceSwitcher multiEurekaServiceSwitcher) {
        Map<String, String> multiEurekaUrlMap = Maps.newConcurrentMap();

        Map<String, String> multiEurekaUrlConfigMap = JsonUtils.fromJson(eurekaUrl, HashMap.class);

        multiEurekaUrlConfigMap.entrySet().forEach(entry -> {
            multiEurekaUrlMap.put(entry.getKey(), entry.getValue());
        });
        multiEurekaServiceSwitcher.setMultiEurekaUrlMap(multiEurekaUrlMap);
    }

    public void initMultiEurekaWithStringConfig(MultiEurekaServiceSwitcher multiEurekaServiceSwitcher) {
        Map<String, String> multiEurekaUrlMap = Maps.newConcurrentMap();
        multiEurekaUrlMap.put("local", eurekaUrl);
        multiEurekaServiceSwitcher.setMultiEurekaUrlMap(multiEurekaUrlMap);
    }

}
