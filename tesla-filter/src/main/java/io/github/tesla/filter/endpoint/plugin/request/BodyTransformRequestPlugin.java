package io.github.tesla.filter.endpoint.plugin.request;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.BodyTransformDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.FreemarkerMapperUtil;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:
 */
@EndpointRequestPlugin(filterType = "BodyTransformRequestPlugin", definitionClazz = BodyTransformDefinition.class,
    filterOrder = 4, filterName = "json request body转换插件")
public class BodyTransformRequestPlugin extends AbstractRequestPlugin {

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        BodyTransformDefinition definition = JsonUtils.json2Definition(filterParam, BodyTransformDefinition.class);
        if (definition == null) {
            return null;
        }
        String tempalteContent = definition.getFtlTemplate();
        try {
            String transformedJson = FreemarkerMapperUtil
                .formatForJsonPath(new String(servletRequest.getRequestBody(), CharsetUtil.UTF_8), tempalteContent);
            ByteBuf bodyContent = Unpooled.copiedBuffer(transformedJson, CharsetUtil.UTF_8);
            // reset body
            final FullHttpRequest realRequest = (FullHttpRequest)realHttpObject;
            realRequest.content().clear().writeBytes(bodyContent);
            HttpUtil.setContentLength(realRequest, bodyContent.readerIndex());
        } catch (Throwable e) {
            PluginUtil.writeFilterLog(BodyTransformRequestPlugin.class, tempalteContent + " is error ", e);
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                "BodyTransform Error,the freemarker template is" + tempalteContent);
        }
        return null;
    }
}
