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
package io.github.tesla.filter.endpoint.plugin.request;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.PathTransformDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@EndpointRequestPlugin(filterType = "PathTransformRequestPlugin", definitionClazz = PathTransformDefinition.class,
    filterOrder = 8, filterName = "path转换插件")
public class PathTransformRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        PathTransformDefinition definition = JsonUtils.json2Definition(filterParam, PathTransformDefinition.class);
        if (definition == null) {
            return null;
        }
        final FullHttpRequest realRequest = (FullHttpRequest)realHttpObject;
        String originalPath =
            AntMatchUtil.path(realRequest.uri()).substring(AntMatchUtil.path(definition.getServicePrefix()).length());
        String changedPath = AntMatchUtil.replacePathWithinPattern(definition.getTransformPath(), originalPath,
            definition.getPatternPath());
        changedPath = AntMatchUtil.concatPath(definition.getServicePrefix(), changedPath);
        LOGGER.info("originalUri: {} ,transformUri: {} ", servletRequest.getNettyRequest().uri(), changedPath);
        realRequest.setUri(changedPath);
        return null;
    }
}
