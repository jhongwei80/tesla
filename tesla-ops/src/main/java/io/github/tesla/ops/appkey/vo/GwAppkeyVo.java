package io.github.tesla.ops.appkey.vo;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GwAppkeyVo
 * @Description TODO
 * @Author zhouchao
 * @Date 2018/12/11 13:26
 * @Version 1.0
 **/
public class GwAppkeyVo implements Serializable {
    private static final long serialVersionUID = 5117067955914426360L;
    private Long id;

    private String appKeyId;

    private String appName;

    private String appKeyDesc;

    private String appKeyEnabled;

    private String appKey;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    private RateLimitDefinitionVo rateLimit = new RateLimitDefinitionVo();

    private List<String> accessServices = new ArrayList<>();

    private QuotaDefinitionVo quota = new QuotaDefinitionVo();

    public List<String> getAccessServices() {
        return accessServices;
    }

    public String getAppKey() {
        return appKey;
    }

    public String getAppKeyDesc() {
        return appKeyDesc;
    }

    public String getAppKeyEnabled() {
        return appKeyEnabled;
    }

    public String getAppKeyId() {
        return appKeyId;
    }

    public String getAppName() {
        return appName;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public Long getId() {
        return id;
    }

    public QuotaDefinitionVo getQuota() {
        return quota;
    }

    public RateLimitDefinitionVo getRateLimit() {
        return rateLimit;
    }

    public void setAccessServices(List<String> accessServices) {
        this.accessServices = accessServices;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public void setAppKeyDesc(String appKeyDesc) {
        this.appKeyDesc = appKeyDesc;
    }

    public void setAppKeyEnabled(String appKeyEnabled) {
        this.appKeyEnabled = appKeyEnabled;
    }

    public void setAppKeyId(String appKeyId) {
        this.appKeyId = appKeyId;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setQuota(QuotaDefinitionVo quota) {
        this.quota = quota;
    }

    public void setRateLimit(RateLimitDefinitionVo rateLimit) {
        this.rateLimit = rateLimit;
    }
}
