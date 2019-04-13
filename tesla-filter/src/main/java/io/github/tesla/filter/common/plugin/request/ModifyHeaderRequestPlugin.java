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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.common.definition.ModifyHeaderDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.annnotation.ServiceRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@ServiceRequestPlugin(filterType = "ModifyHeaderRequestPlugin", definitionClazz = ModifyHeaderDefinition.class,
    filterOrder = 16, filterName = "修改Header插件")
@EndpointRequestPlugin(filterType = "ModifyHeaderRequestPlugin", definitionClazz = ModifyHeaderDefinition.class,
    filterOrder = 9, filterName = "修改Header插件")
public class ModifyHeaderRequestPlugin extends AbstractRequestPlugin {

    /**
     * @desc:
     * @method: doFilter
     * @param: [servletRequest,
     *             realHttpObject, filterParam] filterParam接受json类型字符串
     * @return: io.netty.handler.codec.http.HttpResponse
     * @auther: zhipingzhang
     * @date: 2018/11/20 16:57
     */
    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {

        ModifyHeaderDefinition definition = JsonUtils.json2Definition(filterParam, ModifyHeaderDefinition.class);
        if (definition == null) {
            return null;
        }
        FullHttpRequest realRequest = (FullHttpRequest)realHttpObject;
        if (!CollectionUtils.isEmpty(definition.getRemoveHeader())) {
            definition.getRemoveHeader().forEach(e -> realRequest.headers().remove(e));
        }
        if (!MapUtils.isEmpty(definition.getAddHeader())) {
            definition.getAddHeader().keySet()
                .forEach(e -> realRequest.headers().add(e, definition.getAddHeader().get(e)));
        }
        return null;
    }
}
