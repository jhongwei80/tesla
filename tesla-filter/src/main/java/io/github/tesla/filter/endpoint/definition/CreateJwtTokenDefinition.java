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
public class CreateJwtTokenDefinition extends PluginDefinition {

    private String expiresHeaderKey;

    private String issuer;

    private String claimsHeaderKey;

    private String secretKey;

    private String tokenHeaderKey;

    public String getClaimsHeaderKey() {
        return claimsHeaderKey;
    }

    public String getExpiresHeaderKey() {
        return expiresHeaderKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getTokenHeaderKey() {
        return tokenHeaderKey;
    }

    public void setClaimsHeaderKey(String claimsHeaderKey) {
        this.claimsHeaderKey = claimsHeaderKey;
    }

    public void setExpiresHeaderKey(String expiresHeaderKey) {
        this.expiresHeaderKey = expiresHeaderKey;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public void setTokenHeaderKey(String tokenHeaderKey) {
        this.tokenHeaderKey = tokenHeaderKey;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        CreateJwtTokenDefinition definition = JsonUtils.fromJson(paramJson, CreateJwtTokenDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getClaimsHeaderKey()),
            "createJwtToken-ClaimsHeader不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getExpiresHeaderKey()),
            "createJwtToken-ExpiresHeader不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getIssuer()), "createJwtToken-Issuer不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getSecretKey()), "createJwtToken-SecretKey不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getTokenHeaderKey()),
            "createJwtToken-TokenHeader不可为空");
        return paramJson;
    }
}
