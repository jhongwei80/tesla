package io.github.tesla.filter.common.definition;

import java.util.Map;

public class AccessControlDefinition {
    // key 为services的service_id value 为 prefix
    private Map<String, String> accessServices;

    public Map<String, String> getAccessServices() {
        return accessServices;
    }

    public void setAccessServices(Map<String, String> accessServices) {
        this.accessServices = accessServices;
    }
}
