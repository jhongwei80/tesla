package io.github.tesla.filter.endpoint.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.SnowflakeIdWorker;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class GRpcRoutingDefinition extends PluginDefinition {

    private String serviceName;

    private String methodName;

    private String group;

    private String version;

    private String protoFileId;

    public String getGroup() {
        return group;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getProtoFileId() {
        return protoFileId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getVersion() {
        return version;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setProtoFileId(String protoFileId) {
        this.protoFileId = protoFileId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        GRpcRoutingDefinition definition = JsonUtils.fromJson(paramJson, GRpcRoutingDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getServiceName()), "rpc-grpc路由-服务名称不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getProtoFileId()), "rpc-grpc路由-grpc文件不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getMethodName()), "rpc-grpc路由-方法不可为空");
        if (definition.getProtoFileId().contains(fileTab)) {
            String proFileId = SnowflakeIdWorker.nextId(PluginDefinition.filePrefix);
            Preconditions.checkNotNull(PluginDefinition.uploadFileMap.get().get(definition.getProtoFileId()),
                "rpc-grpc路由-grpc文件不可为空");
            PluginDefinition.uploadFileMap.get().put(proFileId,
                PluginDefinition.uploadFileMap.get().get(definition.getProtoFileId()));
            PluginDefinition.uploadFileMap.get().remove(definition.getProtoFileId());
            definition.setProtoFileId(proFileId);
        }
        return JsonUtils.serializeToJson(definition);
    }
}
