package io.github.tesla.filter.endpoint.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class CreateOauthTokenDefinition extends PluginDefinition {

    private String oauthClientIdHeaderKey;

    private String secretHeaderKey;

    private String tokenHeaderKey;

    public String getOauthClientIdHeaderKey() {
        return oauthClientIdHeaderKey;
    }

    public String getSecretHeaderKey() {
        return secretHeaderKey;
    }

    public String getTokenHeaderKey() {
        return tokenHeaderKey;
    }

    public void setOauthClientIdHeaderKey(String oauthClientIdHeaderKey) {
        this.oauthClientIdHeaderKey = oauthClientIdHeaderKey;
    }

    public void setSecretHeaderKey(String secretHeaderKey) {
        this.secretHeaderKey = secretHeaderKey;
    }

    public void setTokenHeaderKey(String tokenHeaderKey) {
        this.tokenHeaderKey = tokenHeaderKey;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        CreateOauthTokenDefinition definition = JsonUtils.fromJson(paramJson, CreateOauthTokenDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getOauthClientIdHeaderKey()),
            "createOauthToken-OauthClientIdHeader不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getSecretHeaderKey()),
            "createOauthToken-SecretHeader不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getTokenHeaderKey()),
            "createOauthToken-TokenHeader不可为空");
        return paramJson;
    }
}
