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
package io.github.tesla.gateway.config.eureka;

import static io.github.tesla.gateway.config.eureka.util.IdUtils.getDefaultInstanceId;

import java.net.MalformedURLException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.EurekaInstanceConfig;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClientConfig;

import io.github.tesla.filter.support.springcloud.DiscoveryClientWrapper;
import io.github.tesla.filter.support.springcloud.SpringEnvironmentUtil;
import io.github.tesla.gateway.config.eureka.metadata.DefaultManagementMetadataProvider;
import io.github.tesla.gateway.config.eureka.metadata.ManagementMetadata;
import io.github.tesla.gateway.config.eureka.util.InetUtils;
import io.github.tesla.gateway.config.eureka.util.InetUtilsProperties;
import io.github.tesla.gateway.metrics.MetricsHttpServer;

/**
 * @author liushiming
 * @version SpringCloudConfig.java, v 0.0.1 2018年5月6日 上午10:24:11 liushiming
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnClass(EurekaClientConfig.class)
public class EurekaClientAutoConfiguration {

    @Value("${hazelcast.version:1.0.0}")
    private String hazelcastVersion;

    @Bean
    @ConditionalOnMissingBean(value = EurekaClientConfig.class, search = SearchStrategy.CURRENT)
    public EurekaClientConfigBean eurekaClientConfigBean(ConfigurableEnvironment env) {
        EurekaClientConfigBean client = new EurekaClientConfigBean();
        if ("bootstrap".equals(SpringEnvironmentUtil.getProperty(env, "spring.config.name"))) {
            client.setRegisterWithEureka(false);
        }
        return client;
    }

    @Bean
    @ConditionalOnMissingBean(value = EurekaInstanceConfig.class, search = SearchStrategy.CURRENT)
    public EurekaInstanceConfigBean eurekaInstanceConfigBean(InetUtils inetUtils,
        DefaultManagementMetadataProvider managementMetadataProvider, ConfigurableEnvironment env)
        throws MalformedURLException {
        // PropertyResolver environmentPropertyResolver = new RelaxedPropertyResolver(env);
        // PropertyResolver eurekaPropertyResolver = new RelaxedPropertyResolver(env, "eureka.instance.");
        String hostname = SpringEnvironmentUtil.getPropertyWithEureka(env, "hostname");
        boolean preferIpAddress =
            Boolean.parseBoolean(SpringEnvironmentUtil.getPropertyWithEureka(env, "preferIpAddress"));
        String ipAddress = SpringEnvironmentUtil.getPropertyWithEureka(env, "ipAddress");
        boolean isSecurePortEnabled =
            Boolean.parseBoolean(SpringEnvironmentUtil.getPropertyWithEureka(env, "securePortEnabled"));
        String serverContextPath = SpringEnvironmentUtil.getProperty(env, "server.contextPath", "/");
        int serverPort = Integer.valueOf(SpringEnvironmentUtil.getProperty(env, "server.port",
            SpringEnvironmentUtil.getProperty(env, "port", "8080")));
        Integer managementPort = serverPort + 1;// nullable.
        // should be
        // wrapped
        // into
        // optional
        String managementContextPath = SpringEnvironmentUtil.getProperty(env, "management.contextPath");// nullable.
        // should be
        // wrapped into
        // optional
        Integer jmxPort = SpringEnvironmentUtil.getProperty(env, "com.sun.management.jmxremote.port", Integer.class);// nullable
        EurekaInstanceConfigBean instance = new EurekaInstanceConfigBean(inetUtils);
        instance.setNonSecurePort(serverPort);
        instance.setInstanceId(getDefaultInstanceId(env, inetUtils));
        instance.setPreferIpAddress(preferIpAddress);
        instance.setSecurePortEnabled(isSecurePortEnabled);
        if (StringUtils.hasText(ipAddress)) {
            instance.setIpAddress(ipAddress);
        }

        if (isSecurePortEnabled) {
            instance.setSecurePort(serverPort);
        }

        if (StringUtils.hasText(hostname)) {
            instance.setHostname(hostname);
        }
        String statusPageUrlPath = SpringEnvironmentUtil.getPropertyWithEureka(env, "statusPageUrlPath");
        String healthCheckUrlPath = SpringEnvironmentUtil.getPropertyWithEureka(env, "healthCheckUrlPath");

        if (StringUtils.hasText(statusPageUrlPath)) {
            instance.setStatusPageUrlPath(statusPageUrlPath);
        }
        if (StringUtils.hasText(healthCheckUrlPath)) {
            instance.setHealthCheckUrlPath(healthCheckUrlPath);
        }

        ManagementMetadata metadata = managementMetadataProvider.get(instance, serverPort, serverContextPath,
            managementContextPath, managementPort);

        if (metadata != null) {
            instance.setStatusPageUrl(metadata.getStatusPageUrl());
            instance.setHealthCheckUrl(metadata.getHealthCheckUrl());
            if (instance.isSecurePortEnabled()) {
                instance.setSecureHealthCheckUrl(metadata.getSecureHealthCheckUrl());
            }
            Map<String, String> metadataMap = instance.getMetadataMap();
            if (metadataMap.get("management.port") == null) {
                metadataMap.put("management.port", String.valueOf(metadata.getManagementPort()));
            }
            metadataMap.put("gray.enable", "true");
            metadataMap.put("hazelcast.port", System.getProperty("hazelcast.port"));
            metadataMap.put("hazelcast.host", System.getProperty("hazelcast.host"));
            metadataMap.put("hazelcast.version", hazelcastVersion);

            metadataMap.put("management.url", DefaultManagementMetadataProvider.getGreyUrl(instance, serverPort, "",
                MetricsHttpServer.grayApi, managementPort, false));
        }

        setupJmxPort(instance, jmxPort);
        return instance;
    }

    @Bean
    public ApplicationInfoManager getApplicationInfoManager(EurekaInstanceConfigBean instanceConfig) {
        InstanceInfo instanceInfo = new InstanceInfoFactory().create(instanceConfig);
        ApplicationInfoManager applicationInfoManager = new ApplicationInfoManager(instanceConfig, instanceInfo);
        return applicationInfoManager;
    }

    @Bean
    public DiscoveryClientWrapper getDiscoveryClient(ApplicationInfoManager applicationInfoManager,
        EurekaClientConfigBean eurekaClientConfigBean) {

        DiscoveryClientWrapper discoveryClient =
            new DiscoveryClientWrapper(applicationInfoManager, eurekaClientConfigBean);
        applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.UP);
        return discoveryClient;
    }

    @Bean
    @ConditionalOnMissingBean
    public InetUtils inetUtils(InetUtilsProperties properties) {
        return new InetUtils(properties);
    }

    @Bean
    public InetUtilsProperties inetUtilsProperties() {
        return new InetUtilsProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultManagementMetadataProvider serviceManagementMetadataProvider() {
        return new DefaultManagementMetadataProvider();
    }

    private void setupJmxPort(EurekaInstanceConfigBean instance, Integer jmxPort) {
        Map<String, String> metadataMap = instance.getMetadataMap();
        if (metadataMap.get("jmx.port") == null && jmxPort != null) {
            metadataMap.put("jmx.port", String.valueOf(jmxPort));
        }
    }

}
