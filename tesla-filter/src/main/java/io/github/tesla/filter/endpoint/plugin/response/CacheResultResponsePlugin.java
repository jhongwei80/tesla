package io.github.tesla.filter.endpoint.plugin.response;

import java.util.concurrent.TimeUnit;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.endpoint.definition.CacheResultDefinition;
import io.github.tesla.filter.endpoint.plugin.request.CacheResultRequestPlugin;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: zhangzhiping
 * @date: 2018/11/29 17:06
 * @description:
 */
@EndpointResponsePlugin(filterType = "CacheResultPlugin", definitionClazz = CacheResultDefinition.class,
    filterOrder = 10, filterName = "缓存结果插件")
public class CacheResultResponsePlugin extends AbstractResponsePlugin {

    private static byte[] getBytes(ByteBuf buf) {
        byte[] result;
        if (buf.hasArray()) {
            result = buf.array();
        } else {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.getBytes(buf.readerIndex(), bytes);
            result = bytes;
        }
        return result;
    }

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        // 与 CacheResultRequestPlugin 配合，缓存结果,只缓存200的结果
        CacheResultDefinition definition = JsonUtils.json2Definition(filterParam, CacheResultDefinition.class);
        if (definition == null) {
            return httpResponse;
        }
        try {
            if (servletRequest.getAttribute(CacheResultRequestPlugin.cacheHeader) != null
                && HttpResponseStatus.OK.equals(httpResponse.status())) {
                CacheResultRequestPlugin.resultCache.put(
                    (String)servletRequest.getAttribute(CacheResultRequestPlugin.cacheHeader),
                    getBytes(((FullHttpResponse)httpResponse).content().copy()),
                    (Long)servletRequest.getAttribute(CacheResultRequestPlugin.cacheExpire), TimeUnit.SECONDS);
                servletRequest.removeAttribute(CacheResultRequestPlugin.cacheHeader);
                servletRequest.removeAttribute(CacheResultRequestPlugin.cacheExpire);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return httpResponse;
    }
}
