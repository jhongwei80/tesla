package io.github.tesla.filter.service.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:02
 * @description:
 */
public class JWTTokenDefinition extends PluginDefinition {

    private String issuer;

    private String secretKey;

    private String parseClaims;

    private String claimsHeaderKey;

    public String getClaimsHeaderKey() {
        return claimsHeaderKey;
    }

    public String getIssuer() {
        return issuer;
    }

    public String getParseClaims() {
        return parseClaims;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setClaimsHeaderKey(String claimsHeaderKey) {
        this.claimsHeaderKey = claimsHeaderKey;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public void setParseClaims(String parseClaims) {
        this.parseClaims = parseClaims;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        JWTTokenDefinition definition = JsonUtils.fromJson(paramJson, JWTTokenDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getIssuer()), "jwtTokenAuth-issuer不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getSecretKey()), "jwtTokenAuth-SecretKey不可为空");
        if (YesOrNoEnum.YES.getCode().equals(definition.getParseClaims())) {
            Preconditions.checkArgument(StringUtils.isNotBlank(definition.getClaimsHeaderKey()),
                "jwtTokenAuth-claimsHeaderKey不可为空");
        }
        return paramJson;
    }
}
