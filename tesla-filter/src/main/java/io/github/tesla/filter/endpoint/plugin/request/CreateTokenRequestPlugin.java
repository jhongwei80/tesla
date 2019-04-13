package io.github.tesla.filter.endpoint.plugin.request;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.annotation.CreateTokenType;
import io.github.tesla.filter.endpoint.definition.CreateTokenDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.ClassUtils;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: zhangzhiping
 * @date: 2018/11/28 15:45
 * @description:
 */
@EndpointRequestPlugin(filterType = "CreateTokenRequestPlugin", definitionClazz = CreateTokenDefinition.class,
    filterOrder = 1, filterName = "create token 插件")
public class CreateTokenRequestPlugin extends AbstractRequestPlugin {
    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        CreateTokenDefinition createTokenDefinition =
            JsonUtils.json2Definition(filterParam, CreateTokenDefinition.class);

        // 根据注解得到对应的实现filter，并执行
        try {
            CreateTokenRequestPlugin createTokenRequestPlugin = ClassUtils.getSingleBeanWithAnno(
                this.getClass().getPackageName(), CreateTokenType.class, createTokenDefinition.getTokenType(), "value");
            return createTokenRequestPlugin.doFilter(servletRequest, realHttpObject,
                createTokenDefinition.getTokenParamJson());
        } catch (Exception e) {
            PluginUtil.writeFilterLog(CreateTokenRequestPlugin.class, e.getMessage(), e);
            LOGGER.info(e.getMessage());
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                e.getMessage());
        }
    }
}
