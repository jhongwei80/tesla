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
package io.github.tesla.filter.common.plugin.request;

import java.time.Duration;
import java.util.Map;

import com.google.common.collect.Maps;
import com.hazelcast.core.IMap;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.hazelcast.Hazelcast;
import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.common.definition.RateLimitDefinition;
import io.github.tesla.filter.support.annnotation.AppKeyRequestPlugin;
import io.github.tesla.filter.support.annnotation.ServiceRequestPlugin;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@ServiceRequestPlugin(filterType = "RateLimitRequestPlugin", definitionClazz = RateLimitDefinition.class,
    filterOrder = 1, filterName = "访问API限流插件")
@AppKeyRequestPlugin(filterType = "RateLimitRequestPlugin", definitionClazz = RateLimitDefinition.class,
    filterOrder = 1, filterName = "访问API限流插件")
public class RateLimitRequestPlugin extends AbstractRequestPlugin {

    private static ProxyManager<String> buckets;

    private static IMap<String, GridBucketState> bucketStateMap;

    private static Map<String, BucketConfiguration> bucketConfigurationMap = Maps.newConcurrentMap();

    /**
     * @desc:
     * @method: doFilter
     * @param: [servletRequest,realHttpObject,
     *             filterParam] filterParam接受json类型字符串
     * @return: io.netty.handler.codec.http.HttpResponse
     * @auther: zhipingzhang
     * @date: 2018/11/20 16:57
     */
    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        RateLimitDefinition configBean = JsonUtils.json2Definition(filterParam, RateLimitDefinition.class);
        if (configBean == null) {
            return null;
        }
        if (YesOrNoEnum.NO.getCode().equals(configBean.getEnabled())) {
            return null;
        }
        String rateKey = configBean.getAppId().concat("_").concat(String.valueOf(configBean.getRate())).concat("_")
            .concat(String.valueOf(configBean.getPerSeconds()));
        if (bucketStateMap == null) {
            bucketStateMap = getHazelcastInstance().getMap("bucketStateMap");
        }
        if (buckets == null) {
            buckets = Bucket4j.extension(Hazelcast.class).proxyManagerForMap(bucketStateMap);
        }
        BucketConfiguration configuration;
        if (bucketConfigurationMap.get(rateKey) == null) {
            configuration = Bucket4j.configurationBuilder()
                .addLimit(Bandwidth.simple(configBean.getRate(), Duration.ofSeconds(configBean.getPerSeconds())))
                .build();
            bucketConfigurationMap.put(rateKey, configuration);
        } else {
            configuration = bucketConfigurationMap.get(rateKey);
        }
        Bucket bucket = buckets.getProxy(rateKey, configuration);
        if (!bucket.tryConsume(1)) {
            String reason = String.format("rate rule : %s , rateId : %s , uri : %s  too many requests ", rateKey,
                configBean.getAppId(), servletRequest.getRequestURI());
            LOGGER.info(reason);
            PluginUtil.writeFilterLog(RateLimitRequestPlugin.class, reason);
            final HttpRequest nettyRequest = servletRequest.getNettyRequest();
            return PluginUtil.createResponse(HttpResponseStatus.TOO_MANY_REQUESTS, nettyRequest,
                String.format("uri : %s  too many requests ", servletRequest.getRequestURI()));
        }
        return null;
    }
}
