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
package io.github.tesla.filter.support.springcloud;

import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.netflix.appinfo.InstanceInfo;
import com.squareup.okhttp.*;
import com.squareup.okhttp.Request.Builder;

/**
 * @author liushiming
 * @version UserFilter.java, v 0.0.1 2018年5月26日 上午12:33:50 liushiming
 */
public class SpringCloudDiscovery {

    private static Logger LOG = LoggerFactory.getLogger(SpringCloudDiscovery.class);

    private static Pattern HTTP_PREFIX = Pattern.compile("^https?://.*", Pattern.CASE_INSENSITIVE);

    private final OkHttpClient okHttpClient = new OkHttpClient();

    private final DiscoveryClientWrapper discoveryClientWrapper;

    private final int gateWayPort;

    public SpringCloudDiscovery(DiscoveryClientWrapper eurekaClient, int gateWayPort) {
        super();
        this.discoveryClientWrapper = eurekaClient;
        this.gateWayPort = gateWayPort;
    }

    private String buildUrl(String path, String httpHost, int port) {
        final String url;
        if (HTTP_PREFIX.matcher(path).matches()) {
            url = path;
        } else {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            url = String.format("http://%s:%s%s", httpHost, port, path);
        }
        return url;
    }

    public String call(String submitServiceId, Map<String, String> groupVersion, String submitUrl, String submitType,
        String submitJSON, String... headerNamesAndValues) {
        Headers headers = null;
        if (headerNamesAndValues != null && headerNamesAndValues.length > 0) {
            headers = Headers.of(headerNamesAndValues);
        }
        final String httpUrl;
        if (submitServiceId != null) {
            InstanceInfo serviceInstance = this.nextServer(submitServiceId, groupVersion);
            httpUrl = buildUrl(submitUrl, serviceInstance.getHostName(), serviceInstance.getPort());
        } else if (StringUtils.startsWith(submitUrl, "http") || StringUtils.startsWith(submitUrl, "https")) {
            httpUrl = submitUrl;
        } else {
            httpUrl = buildUrl(submitUrl, "localhost", gateWayPort);
        }
        final Response response;
        final Request request;
        try {
            if ("POST".equalsIgnoreCase(submitType) && submitJSON != null) {
                MediaType medialType = MediaType.parse("application/json; charset=utf-8");
                RequestBody requestBody = RequestBody.create(medialType, submitJSON);
                Builder postBuilder = new Request.Builder()//
                    .url(httpUrl);
                if (headers != null) {
                    postBuilder.headers(headers);
                }
                request = postBuilder//
                    .post(requestBody)//
                    .build();
                response = okHttpClient.newCall(request).execute();
            } else {
                Builder builder = new Request.Builder()//
                    .url(httpUrl);
                if (headers != null) {
                    builder.headers(headers);
                }
                request = builder//
                    .get()//
                    .build();
                response = okHttpClient.newCall(request).execute();
            }
            return response.isSuccessful() ? response.body().string() : null;
        } catch (Throwable e) {
            LOG.error("call Remote service error,url is:" + httpUrl + ",body is:" + submitJSON, e);
        }
        return null;
    }

    public String call(String submitServiceId, Pair<String, String> groupVersion, String submitUrl, String submitType,
        String submitJSON, String... headerNamesAndValues) {
        Map<String, String> groupVersionMap = Maps.newHashMap();
        if (groupVersion != null) {
            groupVersionMap.put(DiscoveryClientWrapper.EUREKA_METADATA_GROUP, groupVersion.getLeft());
            groupVersionMap.put(DiscoveryClientWrapper.EUREKA_METADATA_VERSION, groupVersion.getRight());
        }
        return call(submitServiceId, groupVersionMap, submitUrl, submitType, submitJSON, headerNamesAndValues);
    }

    public InstanceInfo nextServer(String serviceId, Map<String, String> groupVersion) {
        if (groupVersion != null) {
            discoveryClientWrapper.setGroupVersion(groupVersion);
        }
        return discoveryClientWrapper.getNextServerFromEureka(serviceId, false);
    }
}
