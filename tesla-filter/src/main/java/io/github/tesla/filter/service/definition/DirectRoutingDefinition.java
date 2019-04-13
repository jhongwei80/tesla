package io.github.tesla.filter.service.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.SnowflakeIdWorker;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class DirectRoutingDefinition extends PluginDefinition {

    private String targetHostPort;

    private String servicePrefix;

    private String targetPrefix;

    private String enableSSL;

    private String selfSignCrtFileId;

    public String getEnableSSL() {
        return enableSSL;
    }

    public String getSelfSignCrtFileId() {
        return selfSignCrtFileId;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public String getTargetHostPort() {
        return targetHostPort;
    }

    public String getTargetPrefix() {
        return targetPrefix;
    }

    public void setEnableSSL(String enableSSL) {
        this.enableSSL = enableSSL;
    }

    public void setSelfSignCrtFileId(String selfSignCrtFileId) {
        this.selfSignCrtFileId = selfSignCrtFileId;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    public void setTargetHostPort(String targetHostPort) {
        this.targetHostPort = targetHostPort;
    }

    public void setTargetPrefix(String targetPrefix) {
        this.targetPrefix = targetPrefix;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        DirectRoutingDefinition definition = JsonUtils.fromJson(paramJson, DirectRoutingDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getTargetHostPort()), "路由目标地址不可为空");
        definition.setServicePrefix(serviceDTO.getServicePrefix());
        if (definition.getSelfSignCrtFileId().contains(fileTab)) {
            String fileId = SnowflakeIdWorker.nextId(PluginDefinition.filePrefix);
            Preconditions.checkNotNull(PluginDefinition.uploadFileMap.get().get(definition.getSelfSignCrtFileId()),
                "直接路由-crt文件不可为空");
            PluginDefinition.uploadFileMap.get().put(fileId,
                PluginDefinition.uploadFileMap.get().get(definition.getSelfSignCrtFileId()));
            PluginDefinition.uploadFileMap.get().remove(definition.getSelfSignCrtFileId());
            definition.setSelfSignCrtFileId(fileId);
        }
        return JsonUtils.serializeToJson(definition);
    }
}
