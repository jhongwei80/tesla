package io.github.tesla.ops.waf.vo;

import java.sql.Timestamp;

import io.github.tesla.filter.support.plugins.WafRequestPluginMetadata;
import io.github.tesla.filter.support.plugins.WafResponsePluginMetadata;

/**
 * @ClassName GwWafVo
 * @Description Gateway waf vo
 * @Author zhouchao
 * @Date 2018/12/13 15:32
 * @Version 1.0
 **/
public class GwWafVo {

    private Long id;

    private String wafName;

    private String wafDesc;

    private String wafEnabled;

    private String wafType;

    private String pluginParam;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPluginParam() {
        return pluginParam;
    }

    public void setPluginParam(String pluginParam) {
        this.pluginParam = pluginParam;
    }

    public String getWafDesc() {
        return wafDesc;
    }

    public void setWafDesc(String wafDesc) {
        this.wafDesc = wafDesc;
    }

    public String getWafEnabled() {
        return wafEnabled;
    }

    public void setWafEnabled(String wafEnabled) {
        this.wafEnabled = wafEnabled;
    }

    public String getWafName() {
        return wafName;
    }

    public void setWafName(String wafName) {
        this.wafName = wafName;
    }

    public String getWafType() {
        return wafType;
    }

    public void setWafType(String wafType) {
        this.wafType = wafType;
    }

    public String validateParam() {
        setPluginParam(WafRequestPluginMetadata.validate(wafType, getPluginParam()));
        setPluginParam(WafResponsePluginMetadata.validate(wafType, getPluginParam()));
        return getPluginParam();
    }
}
