package io.github.tesla.common.dto;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author: zhangzhiping
 * @date: 2018/11/26 17:40
 * @description:
 */
public class AppKeyDTO {
    private Long id;

    private String appKeyId;

    private String appName;

    private String appKeyDesc;

    private String appKeyEnabled;

    private String appKey;

    private List<AppKeyPluginDTO> appKeyPluginDTOS;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

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

    public List<AppKeyPluginDTO> getAppKeyPluginDTOS() {
        return appKeyPluginDTOS;
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

    public void setAppKeyPluginDTOS(List<AppKeyPluginDTO> appKeyPluginDTOS) {
        this.appKeyPluginDTOS = appKeyPluginDTOS;
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
}
