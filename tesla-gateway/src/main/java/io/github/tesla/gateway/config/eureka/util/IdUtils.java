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
package io.github.tesla.gateway.config.eureka.util;

import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import io.github.tesla.filter.support.springcloud.SpringEnvironmentUtil;

/**
 * @author liushiming
 * @version IdUtils.java, v 0.0.1 2018年5月6日 上午10:30:03 liushiming
 */
public class IdUtils {

    private static final String SEPARATOR = ":";

    public static String getDefaultInstanceId(ConfigurableEnvironment env, InetUtils inetUtils) {
        String vcapInstanceId = SpringEnvironmentUtil.getProperty(env, "vcap.application.instance_id");
        if (StringUtils.hasText(vcapInstanceId)) {
            return vcapInstanceId;
        }
        InetUtils.HostInfo hostInfo = inetUtils.findFirstNonLoopbackHostInfo();

        String hostname = hostInfo.getHostname();
        String appName = SpringEnvironmentUtil.getProperty(env, "spring.application.name");

        String namePart = combineParts(hostname, SEPARATOR, appName);

        String indexPart = SpringEnvironmentUtil.getProperty(env, "spring.application.instance_id",
            SpringEnvironmentUtil.getProperty(env, "server.port"));

        return combineParts(namePart, SEPARATOR, indexPart);
    }

    public static String combineParts(String firstPart, String separator, String secondPart) {
        String combined = null;
        if (firstPart != null && secondPart != null) {
            combined = firstPart + separator + secondPart;
        } else if (firstPart != null) {
            combined = firstPart;
        } else if (secondPart != null) {
            combined = secondPart;
        }
        return combined;
    }

}
