package io.github.tesla.filter.endpoint.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class BodyTransformDefinition extends PluginDefinition {

    private String ftlTemplate;

    public String getFtlTemplate() {
        return ftlTemplate;
    }

    public void setFtlTemplate(String ftlTemplate) {
        this.ftlTemplate = ftlTemplate;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        BodyTransformDefinition definition = JsonUtils.fromJson(paramJson, BodyTransformDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getFtlTemplate()), "freeMark模板不可为空");
        return paramJson;
    }
}
