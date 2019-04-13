package io.github.tesla.filter.endpoint.plugin.request;

import com.hazelcast.core.IMap;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.common.definition.CacheConstant;
import io.github.tesla.filter.endpoint.definition.CacheResultDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@EndpointRequestPlugin(filterType = "CacheResultPlugin", definitionClazz = CacheResultDefinition.class,
    filterOrder = 10, filterName = "缓存结果插件")
public class CacheResultRequestPlugin extends AbstractRequestPlugin {

    public static IMap<String, byte[]> resultCache;
    public static final String cacheHeader = "X-Tesla-Cache-Response";
    public static final String cacheExpire = "X-Tesla-Cache-Response-Expire";

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        CacheResultDefinition definition = JsonUtils.json2Definition(filterParam, CacheResultDefinition.class);
        if (definition == null) {
            return null;
        }
        if (resultCache == null) {
            resultCache = getHazelcastInstance().getMap(CacheConstant.CACHE_RESULT_MAP);
        }
        String resultKey = servletRequest.getNettyRequest().uri();
        byte[] result = resultCache.get(resultKey);
        if (result != null) {
            HttpResponse response =
                PluginUtil.createResponse(HttpResponseStatus.OK, servletRequest.getNettyRequest(), result);
            HttpUtil.setKeepAlive(response, false);
            return response;
        } else {
            servletRequest.setAttribute(cacheHeader, resultKey);
            servletRequest.setAttribute(cacheExpire, definition.getExpireSecond());
            return null;
        }
    }
}
