package io.github.tesla.auth.server.config.shiro;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shiro")
public class LadpProp {

    private List<Map<String, String>> ladps;

    public List<Map<String, String>> getLadps() {
        return ladps;
    }

    public void setLadps(List<Map<String, String>> ladps) {
        this.ladps = ladps;
    }
}
