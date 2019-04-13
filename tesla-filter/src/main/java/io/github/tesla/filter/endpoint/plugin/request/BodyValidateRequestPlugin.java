package io.github.tesla.filter.endpoint.plugin.request;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;
import com.jayway.jsonpath.JsonPath;

import io.github.tesla.filter.AbstractRequestPlugin;
import io.github.tesla.filter.endpoint.definition.BodyValidateDefinition;
import io.github.tesla.filter.support.annnotation.EndpointRequestPlugin;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.servlet.NettyHttpServletRequest;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.PluginUtil;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:56
 * @description:请求体校验插件，使用Json schema
 */
@EndpointRequestPlugin(filterType = "BodyValidateRequestPlugin", definitionClazz = BodyValidateDefinition.class,
    filterOrder = 16, filterName = "请求体格式校验插件")
public class BodyValidateRequestPlugin extends AbstractRequestPlugin {

    private static final JsonValidator VALIDATOR = JsonSchemaFactory.byDefault().getValidator();

    @Override
    public HttpResponse doFilter(NettyHttpServletRequest servletRequest, HttpObject realHttpObject,
        Object filterParam) {
        BodyValidateDefinition definition = JsonUtils.json2Definition(filterParam, BodyValidateDefinition.class);
        if (definition == null || definition.getEnable().equals(YesOrNoEnum.NO.getCode())) {
            return null;
        }
        try {
            String requestBody = new String(servletRequest.getRequestBody(), CharsetUtil.UTF_8);
            if (StringUtils.isBlank(requestBody) || !JsonUtils.isJson(requestBody)) {
                return PluginUtil.createResponse(HttpResponseStatus.UNAUTHORIZED, servletRequest.getNettyRequest(),
                    "request body validate error , request body is not json ,please check ");
            }
            String instance;
            if (StringUtils.isBlank(definition.getBodyJsonPath())) {
                instance = requestBody;
            } else {
                try {
                    instance = JsonPath.read(requestBody, definition.getBodyJsonPath());
                } catch (Exception e) {
                    instance = JsonUtils.serializeToJson(JsonPath.read(requestBody, definition.getBodyJsonPath()));
                }
            }
            JsonNode schemaNode = JsonLoader.fromString(definition.getJsonSchema());
            JsonNode instanceNode = JsonLoader.fromString(instance);
            ProcessingReport report = VALIDATOR.validate(schemaNode, instanceNode);
            if (!report.isSuccess()) {
                LOGGER.error(servletRequest.getRequestURI() + " request body validate error : [" + report + "]");
                return PluginUtil.createResponse(HttpResponseStatus.UNAUTHORIZED, servletRequest.getNettyRequest(),
                    "request body validate error , please check your request body and json schema ");
            }
        } catch (IOException | ProcessingException e) {
            PluginUtil.writeFilterLog(BodyValidateRequestPlugin.class,
                servletRequest.getRequestURI() + "request body validate error , please check error info ", e);
            return PluginUtil.createResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR, servletRequest.getNettyRequest(),
                "request body validate error , please check error info : " + e.getMessage());
        }
        return null;
    }
}
