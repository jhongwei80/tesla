package io.github.tesla.common.dto;

import java.sql.Timestamp;

public class CommonPluginDTO {

    protected Long id;

    protected String pluginId;

    protected String pluginName;

    protected String pluginType;

    protected String pluginParam;

    protected Timestamp gmtCreate;

    protected Timestamp gmtModified;

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public Long getId() {
        return id;
    }

    public String getPluginId() {
        return pluginId;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getPluginParam() {
        return pluginParam;
    }

    public String getPluginType() {
        return pluginType;
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

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    public void setPluginParam(String pluginParam) {
        this.pluginParam = pluginParam;
    }

    public void setPluginType(String pluginType) {
        this.pluginType = pluginType;
    }

}
