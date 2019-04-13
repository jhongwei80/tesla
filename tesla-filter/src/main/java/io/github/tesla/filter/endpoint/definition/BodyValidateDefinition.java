package io.github.tesla.filter.endpoint.definition;

import java.io.IOException;

import com.github.fge.jackson.JsonLoader;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class BodyValidateDefinition extends PluginDefinition {

    private String jsonSchema;

    private String bodyJsonPath;

    private String enable;

    public String getJsonSchema() {
        return jsonSchema;
    }

    public void setJsonSchema(String jsonSchema) {
        this.jsonSchema = jsonSchema;
    }

    public String getBodyJsonPath() {
        return bodyJsonPath;
    }

    public void setBodyJsonPath(String bodyJsonPath) {
        this.bodyJsonPath = bodyJsonPath;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        BodyValidateDefinition definition = JsonUtils.fromJson(paramJson, BodyValidateDefinition.class);
        try {
            JsonLoader.fromString(definition.jsonSchema);
        } catch (IOException e) {
            throw new IllegalArgumentException("json schema 不正确：" + e.getMessage());
        }
        return paramJson;
    }
}
