package io.github.tesla.ops.appkey.vo;

public class QuotaDefinitionVo {

    private String appId;

    private Long maxRequest;

    private Long interval;

    /**
     * 0 :SECOND 1 :MINUTE 2 :HOUR 3 :DAY
     */

    private Long timeUtil;

    public String getAppId() {
        return appId;
    }

    public Long getInterval() {
        return interval;
    }

    public Long getMaxRequest() {
        return maxRequest;
    }

    public Long getTimeUtil() {
        return timeUtil;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public void setMaxRequest(Long maxRequest) {
        this.maxRequest = maxRequest;
    }

    public void setTimeUtil(Long timeUtil) {
        this.timeUtil = timeUtil;
    }

}
