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
package io.github.tesla.filter.endpoint.plugin.response;

import io.github.tesla.filter.AbstractResponsePlugin;
import io.github.tesla.filter.endpoint.definition.BodyTransformDefinition;
import io.github.tesla.filter.support.annnotation.EndpointResponsePlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.FreemarkerMapperUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@EndpointResponsePlugin(filterType = "BodyTransformResponsePlugin", definitionClazz = BodyTransformDefinition.class,
    filterOrder = 4, filterName = "response body转换插件")
public class BodyTransformResponsePlugin extends AbstractResponsePlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpResponse httpResponse,
        Object filterParam) {
        if (httpResponse instanceof FullHttpResponse) {
            FullHttpResponse fullHttpResponse = (FullHttpResponse)httpResponse;
            ByteBuf responseBuffer = fullHttpResponse.content();
            Boolean canDataMapping = FreemarkerMapperUtil.isCanDataMapping(responseBuffer);
            BodyTransformDefinition definition = JsonUtils.json2Definition(filterParam, BodyTransformDefinition.class);
            if (definition == null) {
                return httpResponse;
            }
            String tempalteContent = definition.getFtlTemplate();
            if (canDataMapping && tempalteContent != null) {
                try {
                    String transformedJson = FreemarkerMapperUtil
                        .formatForJsonPath(responseBuffer.toString(CharsetUtil.UTF_8), tempalteContent);
                    ByteBuf bodyContent = Unpooled.copiedBuffer(transformedJson, CharsetUtil.UTF_8);
                    // reset body
                    responseBuffer.clear().writeBytes(bodyContent);
                    HttpUtil.setContentLength(fullHttpResponse, bodyContent.readerIndex());
                } catch (Throwable e) {
                    return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR,
                        servletRequest.getNettyRequest(),
                        "BodyTransform Error,The freemark template is " + tempalteContent);
                }
            }
        }
        return httpResponse;
    }

}
