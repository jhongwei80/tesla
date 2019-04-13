package io.github.tesla.filter.support.plugins;

import io.github.tesla.filter.AbstractResponsePlugin;

public class ResponsePluginMetadata extends FilterMetadata {

    protected Class<? extends AbstractResponsePlugin> filterClass;

    public Class<? extends AbstractResponsePlugin> getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class<? extends AbstractResponsePlugin> filterClass) {
        this.filterClass = filterClass;
    }

    public <T> T getInstance() throws Exception {
        return (T)getFilterClass().getDeclaredConstructor().newInstance();
    }
}
