package io.github.tesla.ops.common;

import java.util.Map;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class MultiGatewayUrlSwitcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiGatewayUrlSwitcher.class);
    private static Map<String, String> multiGatewayUrlMap = Maps.newConcurrentMap();

    public static String getGatewayUrl() {
        return multiGatewayUrlMap
            .get(String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("datasource")));
    }

    public static String getGatewayUrl(String key) {
        return multiGatewayUrlMap.get(key);
    }

    public void setMultiGatewayUrlMap(Map<String, String> multiGatewayUrlMap) {
        MultiGatewayUrlSwitcher.multiGatewayUrlMap = multiGatewayUrlMap;
    }
}
