package io.github.tesla.filter.service.definition;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.support.enums.AuthTypeEnum;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 16:02
 * @description:
 */
public class AuthCommonDefinition extends PluginDefinition {

    private String authType;

    private String enabled;

    // 先把具体的configBean转JSON，在放入该属性
    private String authParamJson;

    public String getAuthParamJson() {
        return authParamJson;
    }

    public String getAuthType() {
        return authType;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setAuthParamJson(String authParamJson) {
        this.authParamJson = authParamJson;
    }

    public void setAuthType(String authType) {
        this.authType = authType;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        AuthCommonDefinition definition = JsonUtils.fromJson(paramJson, AuthCommonDefinition.class);
        if (YesOrNoEnum.NO.getCode().equals(definition.getEnabled())) {
            return paramJson;
        }
        definition.setAuthParamJson(
            AuthTypeEnum.validate(definition.getAuthType(), definition.getAuthParamJson(), serviceDTO));
        return JsonUtils.serializeToJson(definition);
    }
}
