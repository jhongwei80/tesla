package io.github.tesla.ops.common;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class MultiEurekaServiceSwitcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiEurekaServiceSwitcher.class);
    private static Map<String, String> multiEurekaUrlMap = Maps.newConcurrentMap();

    public static String getEurekaServiceUrl(String key) {
        return multiEurekaUrlMap.get(key);
    }

    public void setMultiEurekaUrlMap(Map<String, String> multiEurekaUrlMap) {
        MultiEurekaServiceSwitcher.multiEurekaUrlMap = multiEurekaUrlMap;
    }
}
