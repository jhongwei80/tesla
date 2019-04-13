package io.github.tesla.ops.appkey.enums;

public enum AppKeyPluginEnum {
    RateLimitRequestPlugin("RateLimitRequestPlugin", "访问API限流插件"),
    QuotaRequestPlugin("QuotaRequestPlugin", "访问API总流量限制插件"),
    AccessControlRequestPlugin("AccessControlRequestPlugin", "访问权限插件");

    private String filterType;
    private String filterName;

    AppKeyPluginEnum(String pluginType, String filterName) {
        this.filterType = pluginType;
        this.filterName = filterName;
    }

    public String getFilterType() {
        return filterType;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }
}
