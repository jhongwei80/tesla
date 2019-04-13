package io.github.tesla.filter.endpoint.definition;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/29 17:22
 * @description:
 */
public class GroovyExecuteDefinition extends PluginDefinition {

    private String groovyScript;

    public String getGroovyScript() {
        return groovyScript;
    }

    public void setGroovyScript(String groovyScript) {
        this.groovyScript = groovyScript;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        GroovyExecuteDefinition definition = JsonUtils.fromJson(paramJson, GroovyExecuteDefinition.class);
        Preconditions.checkArgument(StringUtils.isNotBlank(definition.getGroovyScript()), "groovy脚本不可为空");
        return paramJson;
    }
}
