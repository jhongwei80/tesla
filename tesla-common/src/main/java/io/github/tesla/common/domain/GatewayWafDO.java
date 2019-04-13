package io.github.tesla.common.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.*;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
@TableName("gateway_waf")
public class GatewayWafDO implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long id;

    private String wafId;

    private String wafName;

    private String wafDesc;

    private String wafEnabled;

    private String wafType;

    @TableField(strategy = FieldStrategy.IGNORED)
    private String pluginParam;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWafId() {
        return wafId;
    }

    public void setWafId(String wafId) {
        this.wafId = wafId;
    }

    public String getWafName() {
        return wafName;
    }

    public void setWafName(String wafName) {
        this.wafName = wafName;
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

    public String getWafType() {
        return wafType;
    }

    public void setWafType(String wafType) {
        this.wafType = wafType;
    }

    public String getPluginParam() {
        return pluginParam;
    }

    public void setPluginParam(String pluginParam) {
        this.pluginParam = pluginParam;
    }

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
}
