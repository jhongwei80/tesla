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
public class PathTransformDefinition extends PluginDefinition {

    private String patternPath;

    private String transformPath;

    private String servicePrefix;

    public String getPatternPath() {
        return patternPath;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public String getTransformPath() {
        return transformPath;
    }

    public void setPatternPath(String patternPath) {
        this.patternPath = patternPath;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    public void setTransformPath(String transformPath) {
        this.transformPath = transformPath;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        PathTransformDefinition definition = JsonUtils.fromJson(paramJson, PathTransformDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getPatternPath()), "path转换表达式不可为空");
        definition.setServicePrefix(serviceDTO.getServicePrefix());
        definition.setTransformPath(endpointDTO.getEndpointUrl());
        return JsonUtils.serializeToJson(definition);
    }
}
