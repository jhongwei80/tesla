package io.github.tesla.filter.support.plugins;

import io.github.tesla.filter.AbstractRequestPlugin;

public class RequestPluginMetadata extends FilterMetadata {

    protected Class<? extends AbstractRequestPlugin> filterClass;

    public Class<? extends AbstractRequestPlugin> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends AbstractRequestPlugin> filterClass) {
        this.filterClass = filterClass;
    }

    public <T> T getInstance() throws Exception {
        return (T)getFilterClass().getDeclaredConstructor().newInstance();
    }
}
