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
public class SpringCloudRoutingDefinition extends PluginDefinition {

    private String serviceName;

    private String group;

    private String version;

    private String servicePrefix;

    private String targetPrefix;

    public String getGroup() {
        return group;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public String getTargetPrefix() {
        return targetPrefix;
    }

    public String getVersion() {
        return version;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    public void setTargetPrefix(String targetPrefix) {
        this.targetPrefix = targetPrefix;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        SpringCloudRoutingDefinition definition = JsonUtils.fromJson(paramJson, SpringCloudRoutingDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getServiceName()), "路由服务名称不可为空");
        definition.setServicePrefix(serviceDTO.getServicePrefix());
        return JsonUtils.serializeToJson(definition);
    }
}
