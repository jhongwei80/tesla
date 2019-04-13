package io.github.tesla.filter.service.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:02
 * @description:
 */
public class CorsDefinition extends PluginDefinition {

    private String allowedOrigins;

    private String allowCredentials;

    private String[] allowedRequestMethods;

    private String allowedRequestHeaders;

    public String getAllowCredentials() {
        return allowCredentials;
    }

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public String getAllowedRequestHeaders() {
        return allowedRequestHeaders;
    }

    public String[] getAllowedRequestMethods() {
        return allowedRequestMethods;
    }

    public void setAllowCredentials(String allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public void setAllowedRequestHeaders(String allowedRequestHeaders) {
        this.allowedRequestHeaders = allowedRequestHeaders;
    }

    public void setAllowedRequestMethods(String[] allowedRequestMethods) {
        this.allowedRequestMethods = allowedRequestMethods;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        CorsDefinition definition = JsonUtils.json2Definition(paramJson, CorsDefinition.class);
        if (definition.getAllowedOrigins().contains("*")) {
            definition.setAllowedOrigins("*");
        }
        if (definition.getAllowedRequestHeaders().contains("*")) {
            definition.setAllowedRequestHeaders("*");
        }
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getAllowedOrigins()),
            "cors allowed origins 不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getAllowedRequestHeaders()),
            "cors allowed headers 不可为空");
        Preconditions.checkArgument(
            definition.getAllowedRequestMethods() != null && definition.getAllowedRequestMethods().length > 0,
            "cors allowed methods 不可为空");
        return JsonUtils.serializeToJson(definition);
    }
}
