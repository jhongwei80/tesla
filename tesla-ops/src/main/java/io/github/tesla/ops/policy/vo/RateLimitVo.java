package io.github.tesla.ops.policy.vo;

import java.io.Serializable;

/**
 * @ClassName RateLimit
 * @Description Rate limit vo
 * @Author zhouchao
 * @Date 2018/12/10 21:30
 * @Version 1.0
 **/
public class RateLimitVo implements Serializable {
    private static final long serialVersionUID = -5500547269851837251L;
    private Long rate;
    private Long perSeconds;
    private String enabled;

    public String getEnabled() {
        return enabled;
    }

    public Long getPerSeconds() {
        return perSeconds;
    }

    public Long getRate() {
        return rate;
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
