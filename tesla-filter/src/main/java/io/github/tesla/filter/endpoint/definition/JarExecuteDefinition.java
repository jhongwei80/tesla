package io.github.tesla.filter.endpoint.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.SnowflakeIdWorker;

public class JarExecuteDefinition extends PluginDefinition {

    private String fileId;

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        JarExecuteDefinition definition = JsonUtils.fromJson(paramJson, JarExecuteDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getFileId()), "jar包执行插件-jar文件不可为空");
        if (definition.getFileId().contains(fileTab)) {
            String jarFileId = SnowflakeIdWorker.nextId(PluginDefinition.filePrefix);
            Preconditions.checkNotNull(PluginDefinition.uploadFileMap.get().get(definition.getFileId()),
                "jar包执行插件-jar文件不可为空");
            PluginDefinition.uploadFileMap.get().put(jarFileId,
                PluginDefinition.uploadFileMap.get().get(definition.getFileId()));
            PluginDefinition.uploadFileMap.get().remove(definition.getFileId());
            definition.setFileId(jarFileId);
        }
        return JsonUtils.serializeToJson(definition);
    }
}
