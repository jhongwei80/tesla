/*
 * Copyright (c) 2018 DISID CORPORATION S.L.
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

package io.github.tesla.filter.endpoint.plugin.request;

import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.GroovyExecuteDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.GroovyCompiler;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/29 17:06
 * @description:
 */
@EndpointRequestPlugin(filterType = "GroovyExecuteRequestPlugin", filterOrder = 7, filterName = "groovy脚本执行插件",
    definitionClazz = GroovyExecuteDefinition.class)
public class GroovyExecuteRequestPlugin extends AbstractRequestPlugin {

    private static final Map<String, GroovyExecuteRequestPlugin> groovyInstance = Maps.newConcurrentMap();

    /**
     * @desc: 脚本的代码需要继承 GroovyExecuteRequestPlugin 并实现下面的方法
     * @method: doFilter
     * @param: [servletRequest,
     *             realHttpObject]
     * @return: io.netty.handler.codec.http.HttpResponse
     * @auther: zhipingzhang
     * @date: 2018/11/29 17:05
     */
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject) {
        return null;
    }

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        HttpResponse response = null;
        GroovyExecuteDefinition definition = JsonUtils.json2Definition(filterParam, GroovyExecuteDefinition.class);
        if (definition == null) {
            return null;
        }
        if (StringUtils.isNotBlank(definition.getGroovyScript())) {
            GroovyExecuteRequestPlugin userFilter = groovyInstance.get(definition.getGroovyScript());
            if (userFilter == null) {
                try {
                    Class<?> clazz = GroovyCompiler.compile(definition.getGroovyScript());
                    if (clazz != null && GroovyExecuteRequestPlugin.class.isAssignableFrom(clazz)
                        && !Modifier.isAbstract(clazz.getModifiers()) && !clazz.isInterface()) {
                        userFilter = (GroovyExecuteRequestPlugin)clazz.getDeclaredConstructor().newInstance();
                        userFilter.setSpringCloudDiscovery(this.getSpringCloudDiscovery());
                        groovyInstance.put(definition.getGroovyScript(), userFilter);
                    }
                } catch (Throwable e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
            if (userFilter != null) {
                HttpResponse userResponse = userFilter.doFilter(servletRequest, realHttpObject);
                if (userResponse != null) {
                    response = userResponse;
                }
            }
        }
        return response;
    }

}
