package io.github.tesla.filter.common.definition;

public class QuotaDefinition {

    private String appId;

    private long maxRequest;

    private long interval;

    /**
     * 0 :SECOND 1 :MINUTE 2 :HOUR 3 :DAY
     */

    private long timeUtil;

    public String getAppId() {
        return appId;
    }

    public long getInterval() {
        return interval;
    }

    public long getIntervalMillis() {
        if (timeUtil == 0) {
            return interval * 1000;
        }
        if (timeUtil == 1) {
            return interval * 60 * 1000;
        }
        if (timeUtil == 2) {
            return interval * 60 * 60 * 1000;
        }
        if (timeUtil == 3) {
            return interval * 24 * 60 * 60 * 1000;
        }
        return 0;
    }

    public long getMaxRequest() {
        return maxRequest;
    }

    public long getTimeUtil() {
        return timeUtil;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public void setMaxRequest(long maxRequest) {
        this.maxRequest = maxRequest;
    }

    public void setTimeUtil(long timeUtil) {
        this.timeUtil = timeUtil;
    }
}
