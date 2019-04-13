package io.github.tesla.gateway.netty.filter;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.tesla.common.service.SpringContextHolder;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.PluginUtil;
import io.github.tesla.gateway.cache.FilterCache;
import io.github.tesla.gateway.excutor.ServiceExecutor;
import io.github.tesla.gateway.excutor.ServiceRequestPluginExecutor;
import io.github.tesla.gateway.excutor.WafRequestPluginExecutor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HttpRequestFilterChain {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpRequestFilterChain.class);

    public static HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject httpObject,
        ChannelHandlerContext channelHandlerContext) {
        FilterCache cacheComponent = SpringContextHolder.getBean(FilterCache.class);
        List<WafRequestPluginExecutor> wafRequests = cacheComponent.loadWafRequestPlugins();
        // 执行waf过滤器
        for (Iterator<WafRequestPluginExecutor> it = wafRequests.iterator(); it.hasNext();) {
            WafRequestPluginExecutor plugin = it.next();
            LOGGER.debug("do filter,the name is:" + plugin.getFilterName());
            HttpResponse response = plugin.doFilter(servletRequest, httpObject);
            if (response != null) {
                LOGGER.debug("hit " + response);
                return response;
            }
        }
        ServiceExecutor serviceCache = cacheComponent.loadServiceCache(servletRequest.getRequestURI());
        if (serviceCache == null) {
            return PluginUtil.createResponse(HttpResponseStatus.NOT_FOUND, servletRequest.getNettyRequest(),
                " not found match router config ");
        }
        List<ServiceRequestPluginExecutor> serviceRequests =
            serviceCache.matchAndGetRequestFiltes(servletRequest.getRequestURI(), servletRequest.getMethod());
        // 未匹配到endpoint
        if (serviceRequests == null) {
            return PluginUtil.createResponse(HttpResponseStatus.NOT_FOUND, servletRequest.getNettyRequest(),
                " not found match endpoint config ");
        }
        // 执行service and endpoint级别过滤器
        for (Iterator<ServiceRequestPluginExecutor> it = serviceRequests.iterator(); it.hasNext();) {
            ServiceRequestPluginExecutor plugin = it.next();
            LOGGER.debug("do filter,the name is:" + plugin.getFilterName());
            HttpResponse response = plugin.doFilter(servletRequest, httpObject);
            if (response != null) {
                LOGGER.debug("hit " + response);
                return response;
            }
        }
        return null;
    }
}
