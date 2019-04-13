package io.github.tesla.filter.endpoint.definition;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.enums.RouteTypeEnum;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class RpcRoutingDefinition extends PluginDefinition {

    private String rpcType;

    private String rpcParamJson;

    public String getRpcParamJson() {
        return rpcParamJson;
    }

    public String getRpcType() {
        return rpcType;
    }

    public void setRpcParamJson(String rpcParamJson) {
        this.rpcParamJson = rpcParamJson;
    }

    public void setRpcType(String rpcType) {
        this.rpcType = rpcType;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        RpcRoutingDefinition definition = JsonUtils.fromJson(paramJson, RpcRoutingDefinition.class);
        definition.setRpcParamJson(
            RouteTypeEnum.validate(definition.getRpcType(), definition.getRpcParamJson(), serviceDTO, endpointDTO));
        return JsonUtils.serializeToJson(definition);
    }

}
