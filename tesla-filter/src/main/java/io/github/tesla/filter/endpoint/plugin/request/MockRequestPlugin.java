package io.github.tesla.filter.endpoint.plugin.request;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.MockDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.FreemarkerMapperUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@EndpointRequestPlugin(filterType = "MockRequestPlugin", definitionClazz = MockDefinition.class, filterOrder = 11,
    filterName = "mock插件")
public class MockRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        MockDefinition definition = JsonUtils.json2Definition(filterParam, MockDefinition.class);
        if (definition == null || YesOrNoEnum.NO.getCode().equals(definition.getEnable())) {
            return null;
        }
        String transformedJson = null;
        try {
            String requestBody = new String(servletRequest.getRequestBody(), CharsetUtil.UTF_8);
            if (StringUtils.isBlank(requestBody)) {
                transformedJson = definition.getResultTemplate();
            } else {
                transformedJson = FreemarkerMapperUtil.formatForJsonPath(requestBody, definition.getResultTemplate());
            }
            if (transformedJson != null) {
                HttpResponse response =
                    PluginUtil.createResponse(HttpResponseStatus.OK, servletRequest.getNettyRequest(), transformedJson);
                HttpUtil.setKeepAlive(response, false);
                return response;
            }
        } catch (IOException e) {
            PluginUtil.writeFilterLog(MockRequestPlugin.class,
                servletRequest.getRequestURI() + "mock error , request to backend ", e);
        }
        return null;
    }
}
