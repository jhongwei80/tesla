package io.github.tesla.filter.endpoint.definition;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.enums.CreateTokenTypeEnum;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class CreateTokenDefinition extends PluginDefinition {

    private String tokenType;

    // 先把具体的configBean转JSON，在放入该属性
    private String tokenParamJson;

    public String getTokenParamJson() {
        return tokenParamJson;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenParamJson(String tokenParamJson) {
        this.tokenParamJson = tokenParamJson;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        CreateTokenDefinition definition = JsonUtils.fromJson(paramJson, CreateTokenDefinition.class);
        definition.setTokenParamJson(
            CreateTokenTypeEnum.validate(definition.getTokenType(), definition.getTokenParamJson(), serviceDTO));
        return JsonUtils.serializeToJson(definition);
    }
}
