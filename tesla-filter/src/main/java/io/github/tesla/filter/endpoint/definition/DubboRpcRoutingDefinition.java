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
public class DubboRpcRoutingDefinition extends PluginDefinition {

    private String serviceName;

    private String methodName;

    private String group;

    private String version;

    private String dubboParamTemplate;

    public String getDubboParamTemplate() {
        return dubboParamTemplate;
    }

    public String getGroup() {
        return group;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setDubboParamTemplate(String dubboParamTemplate) {
        this.dubboParamTemplate = dubboParamTemplate;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        DubboRpcRoutingDefinition definition = JsonUtils.fromJson(paramJson, DubboRpcRoutingDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getServiceName()), "rpc-dubbo路由-服务名称不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getDubboParamTemplate()),
            "rpc-dubbo路由-dubbo参数模板不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getMethodName()), "rpc-dubbo路由-服务方法不可为空");
        return paramJson;
    }
}
