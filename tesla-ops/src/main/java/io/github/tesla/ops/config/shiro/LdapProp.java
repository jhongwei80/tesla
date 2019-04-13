package io.github.tesla.ops.config.shiro;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shiro")
public class LdapProp {

    private List<Map<String, String>> ldaps;

    public List<Map<String, String>> getLdaps() {
        return ldaps;
    }

    public void setLdaps(List<Map<String, String>> ldaps) {
        this.ldaps = ldaps;
    }
}
