package io.github.tesla.filter.endpoint.plugin.request;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.JarExecuteDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.ClassUtils;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/29 16:08
 * @description:
 */
@EndpointRequestPlugin(filterType = "JarExecuteRequestPlugin", definitionClazz = JarExecuteDefinition.class,
    filterOrder = 6, filterName = "执行上传Jar包插件")
public class JarExecuteRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        JarExecuteDefinition definition = JsonUtils.json2Definition(filterParam, JarExecuteDefinition.class);
        if (definition == null) {
            return null;
        }
        JarExecuteRequestPlugin userFilter = ClassUtils.getUserJarFilterRule(JarExecuteRequestPlugin.class.getName(),
            definition.getFileId(), getFileBytesByKey(definition.getFileId()));
        if (userFilter == null) {
            LOGGER.error(" request not found jar file ,fileId:" + definition.getFileId());
            return null;
        }
        userFilter.setSpringCloudDiscovery(this.getSpringCloudDiscovery());
        HttpResponse userResponse = userFilter.doFilter(servletRequest, (HttpRequest)realHttpObject);
        return userResponse;
    }

    /**
     * @desc: 上传的Jar包执行类需实现该方法
     * @method: doFilter
     * @param: [servletRequest,
     *             realHttpObject]
     * @return: io.netty.handler.codec.http.HttpResponse
     * @auther: zhipingzhang
     * @date: 2018/11/29 14:36
     */
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpRequest realHttpRequest) {
        return null;
    }
}
