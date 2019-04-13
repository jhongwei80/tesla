package io.github.tesla.gateway.netty.filter;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.gateway.cache.FilterCache;
import io.github.tesla.gateway.excutor.ServiceExecutor;
import io.github.tesla.gateway.excutor.ServiceResponsePluginExecutor;
import io.github.tesla.gateway.excutor.WafResponsePluginExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponse;

public class HttpResponseFilterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponseFilterChain.class);

    public static HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        ChannelHandlerContext channelHandlerContext) {
        FilterCache cacheComponent = SpringContextHolder.getBean(FilterCache.class);
        List<WafResponsePluginExecutor> wafResponses = cacheComponent.loadWafResonsePlugins();
        // 执行waf过滤器
        for (Iterator<WafResponsePluginExecutor> it = wafResponses.iterator(); it.hasNext();) {
            WafResponsePluginExecutor plugin = it.next();
            LOGGER.debug("do filter,the name is:" + plugin.getFilterName());
            httpResponse = plugin.doFilter(servletRequest, httpResponse);
        }
        ServiceExecutor serviceCache = cacheComponent.loadServiceCache(servletRequest.getRequestURI());
        if (serviceCache == null) {
            return httpResponse;
        }
        List<ServiceResponsePluginExecutor> serviceRequests =
            serviceCache.matchAndGetResponseFiltes(servletRequest.getRequestURI(), servletRequest.getMethod());
        // 执行service and endpoint级别过滤器
        for (Iterator<ServiceResponsePluginExecutor> it = serviceRequests.iterator(); it.hasNext();) {
            ServiceResponsePluginExecutor plugin = it.next();
            LOGGER.debug("do filter,the name is:" + plugin.getFilterName());
            httpResponse = plugin.doFilter(servletRequest, httpResponse);
        }
        return httpResponse;
    }

}
