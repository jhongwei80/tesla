package io.github.tesla.filter.service.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class StandardOauthDefinition extends PluginDefinition {

    private String oauthServerUrl;

    private String tokenHeader;

    public String getOauthServerUrl() {
        return oauthServerUrl;
    }

    public String getTokenHeader() {
        return tokenHeader;
    }

    public void setOauthServerUrl(String oauthServerUrl) {
        this.oauthServerUrl = oauthServerUrl;
    }

    public void setTokenHeader(String tokenHeader) {
        this.tokenHeader = tokenHeader;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        StandardOauthDefinition definition = JsonUtils.fromJson(paramJson, StandardOauthDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getOauthServerUrl()), "oauth server地址不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getTokenHeader()), "token header定义不可为空");
        return paramJson;
    }
}
