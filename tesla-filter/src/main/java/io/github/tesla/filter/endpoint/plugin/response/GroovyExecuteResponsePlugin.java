package io.github.tesla.filter.endpoint.plugin.response;

import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.endpoint.definition.GroovyExecuteDefinition;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.GroovyCompiler;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/29 17:06
 * @description:
 */
@EndpointResponsePlugin(filterType = "GroovyExecuteResponsePlugin", filterOrder = 6, filterName = "groovy脚本执行插件")
public class GroovyExecuteResponsePlugin extends AbstractResponsePlugin {

    private static final Map<String, GroovyExecuteResponsePlugin> groovyInstance = Maps.newConcurrentMap();

    /**
     * @desc: 脚本的代码需要继承 GroovyExecuteResponsePlugin 并实现下面的方法
     * @method: doFilter
     * @param: [servletRequest,
     *             realHttpObject]
     * @return: io.netty.handler.codec.http.HttpResponse
     * @auther: zhipingzhang
     * @date: 2018/11/29 17:05
     */
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse) {
        return httpResponse;
    }

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        HttpResponse response = httpResponse;
        GroovyExecuteDefinition definition = JsonUtils.json2Definition(filterParam, GroovyExecuteDefinition.class);
        if (definition == null) {
            return httpResponse;
        }
        if (StringUtils.isNotBlank(definition.getGroovyScript())) {
            GroovyExecuteResponsePlugin userFilter = groovyInstance.get(definition.getGroovyScript());
            if (userFilter == null) {
                try {
                    Class<?> clazz = GroovyCompiler.compile(definition.getGroovyScript());
                    if (clazz != null && GroovyExecuteResponsePlugin.class.isAssignableFrom(clazz)
                        && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()) {

                        userFilter = (GroovyExecuteResponsePlugin)clazz.newInstance();
                        userFilter.setSpringCloudDiscovery(this.getSpringCloudDiscovery());
                        groovyInstance.put(definition.getGroovyScript(), userFilter);
                    }
                } catch (Throwable e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (userFilter != null) {
                HttpResponse userResponse = userFilter.doFilter(servletRequest, httpResponse);
                if (userResponse != null) {
                    response = userResponse;
                }
            }
        }
        return response;
    }

}
