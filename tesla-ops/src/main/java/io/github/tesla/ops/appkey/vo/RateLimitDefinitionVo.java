package io.github.tesla.ops.appkey.vo;

public class RateLimitDefinitionVo {

    private String appId;

    private Long rate;

    private Long perSeconds;

    private String enabled;

    public String getAppId() {
        return appId;
    }

    public String getEnabled() {
        return enabled;
    }

    public Long getPerSeconds() {
        return perSeconds;
    }

    public Long getRate() {
        return rate;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public void setPerSeconds(Long perSeconds) {
        this.perSeconds = perSeconds;
    }

    public void setRate(Long rate) {
        this.rate = rate;
    }

}
