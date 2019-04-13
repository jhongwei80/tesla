package io.github.tesla.filter.common.definition;

import java.util.Map;
import java.util.Set;

import io.github.tesla.filter.service.definition.PluginDefinition;

/**
 * @author: zhangzhiping
 * @date: 2018/12/4 10:58
 * @description:
 */
public class ModifyHeaderDefinition extends PluginDefinition {

    private Map<String, String> addHeader;

    private Set<String> removeHeader;

    public Map<String, String> getAddHeader() {
        return addHeader;
    }

    public Set<String> getRemoveHeader() {
        return removeHeader;
    }

    public void setAddHeader(Map<String, String> addHeader) {
        this.addHeader = addHeader;
    }

    public void setRemoveHeader(Set<String> removeHeader) {
        this.removeHeader = removeHeader;
    }
}
