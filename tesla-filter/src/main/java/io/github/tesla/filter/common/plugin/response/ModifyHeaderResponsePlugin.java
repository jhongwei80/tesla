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
package io.github.tesla.filter.common.plugin.response;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.common.definition.ModifyHeaderDefinition;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.support.annnotation.ServiceResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@ServiceResponsePlugin(filterType = "ModifyHeaderResponsePlugin", definitionClazz = ModifyHeaderDefinition.class,
    filterOrder = 3, filterName = "修改Header插件")
@EndpointResponsePlugin(filterType = "ModifyHeaderResponsePlugin", definitionClazz = ModifyHeaderDefinition.class,
    filterOrder = 9, filterName = "修改Header插件")
public class ModifyHeaderResponsePlugin extends AbstractResponsePlugin {

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
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {

        ModifyHeaderDefinition definition = JsonUtils.json2Definition(filterParam, ModifyHeaderDefinition.class);
        if (definition == null) {
            return httpResponse;
        }
        FullHttpResponse response = (FullHttpResponse)httpResponse;
        if (!CollectionUtils.isEmpty(definition.getRemoveHeader())) {
            definition.getRemoveHeader().forEach(e -> response.headers().remove(e));
        }
        if (!MapUtils.isEmpty(definition.getAddHeader())) {
            definition.getAddHeader().keySet()
                .forEach(e -> response.headers().add(e, definition.getAddHeader().get(e)));
        }
        return httpResponse;
    }

}
