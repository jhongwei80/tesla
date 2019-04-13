package io.github.tesla.ops.utils;

import java.util.Map;

import org.apache.shiro.SecurityUtils;

import com.google.common.collect.Maps;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClient;
import com.netflix.discovery.shared.transport.jersey.EurekaJerseyClientImpl;
import com.netflix.discovery.shared.transport.jersey.JerseyApplicationClient;
import com.sun.jersey.api.client.filter.GZIPContentEncodingFilter;
import com.sun.jersey.client.apache4.ApacheHttpClient4;

import io.github.tesla.ops.common.MultiEurekaServiceSwitcher;

public class EurekaClientUtil {

    private static ApacheHttpClient4 discoveryApacheClient;

    static {
        EurekaJerseyClientImpl.EurekaJerseyClientBuilder clientBuilder =
            new EurekaJerseyClientImpl.EurekaJerseyClientBuilder().withClientName("DiscoveryClient-HTTPClient")
                .withUserAgent("Java-EurekaClient").withConnectionTimeout(5000).withReadTimeout(8000)
                .withMaxConnectionsPerHost(50).withMaxTotalConnections(200).withConnectionIdleTimeout(30000)
                .withEncoderWrapper(null).withDecoderWrapper(null);

        EurekaJerseyClient jerseyClient = clientBuilder.build();
        discoveryApacheClient = jerseyClient.getClient();
        addFilters(discoveryApacheClient);
    }

    private static final Map<String, JerseyApplicationClient> clientMap = Maps.newConcurrentMap();

    public static Applications getApplications() {
        return getApplications(MultiEurekaServiceSwitcher
            .getEurekaServiceUrl(String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("datasource"))));
    }

    public static Application getApplication(String appName) {
        return getApplication(MultiEurekaServiceSwitcher.getEurekaServiceUrl(
            String.valueOf(SecurityUtils.getSubject().getSession().getAttribute("datasource"))), appName);
    }

    public static Application getApplication(String serverUrl, String appName) {
        if (StringUtils.isBlank(serverUrl)) {
            return null;
        }
        if (clientMap.get(serverUrl) == null) {
            synchronized (EurekaClientUtil.class) {
                if (clientMap.get(serverUrl) == null) {
                    clientMap.put(serverUrl, new JerseyApplicationClient(discoveryApacheClient, serverUrl, null));
                }
            }
        }
        Application app = null;
        EurekaHttpResponse<Application> response = clientMap.get(serverUrl).getApplication(appName);
        if (response.getStatusCode() == 200) {
            app = response.getEntity();
        }
        return app;
    }

    public static Applications getApplications(String serverUrl) {
        if (StringUtils.isBlank(serverUrl)) {
            return null;
        }
        if (clientMap.get(serverUrl) == null) {
            synchronized (EurekaClientUtil.class) {
                if (clientMap.get(serverUrl) == null) {
                    clientMap.put(serverUrl, new JerseyApplicationClient(discoveryApacheClient, serverUrl, null));
                }
            }
        }
        Applications apps = null;
        EurekaHttpResponse<Applications> response = clientMap.get(serverUrl).getApplications();
        if (response.getStatusCode() == 200) {
            apps = response.getEntity();
        }
        return apps;
    }

    private static void addFilters(ApacheHttpClient4 discoveryApacheClient) {
        // Add gzip content encoding support
        discoveryApacheClient.addFilter(new GZIPContentEncodingFilter(false));
    }
}
