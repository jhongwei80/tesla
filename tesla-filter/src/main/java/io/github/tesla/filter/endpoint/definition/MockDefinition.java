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
public class MockDefinition extends PluginDefinition {

    // Y-一直启用 N-禁用
    private String enable;

    private String resultTemplate;

    public String getEnable() {
        return enable;
    }

    public String getResultTemplate() {
        return resultTemplate;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public void setResultTemplate(String resultTemplate) {
        this.resultTemplate = resultTemplate;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        MockDefinition definition = JsonUtils.fromJson(paramJson, MockDefinition.class);
        Preconditions.checkArgument(definition.enable != null, "mock 开启状态不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getResultTemplate()), "mock 返回内容配置不可为空");
        return paramJson;
    }
}
