package io.github.tesla.ops.gray.domain;

import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 灰度计划
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:50
 * @version: V1.0.0
 * @since JDK 11
 */
@TableName("gateway_gray_plan")
public class GrayPlanDO extends BaseDO {

    /**
     * 计划名称
     */
    @TableField("plan_name")
    private String planName;

    /**
     * 计划描述
     */
    @TableField("plan_desc")
    private String planDesc;

    /**
     * 计划负责人
     */
    @TableField("plan_owner")
    private String planOwner;

    /**
     * 是否启用
     */
    @TableField("enable")
    private String enable;

    /**
     * 生效时间
     */
    @TableField("effect_time")
    private Timestamp effectTime;

    /**
     * 失效时间
     */
    @TableField("expire_time")
    private Timestamp expireTime;

    /**
     * 创建者
     */
    @TableField("create_user")
    private String createUser;

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanDesc() {
        return planDesc;
    }

    public void setPlanDesc(String planDesc) {
        this.planDesc = planDesc;
    }

    public String getPlanOwner() {
        return planOwner;
    }

    public void setPlanOwner(String planOwner) {
        this.planOwner = planOwner;
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public Timestamp getEffectTime() {
        return effectTime;
    }

    public void setEffectTime(Timestamp effectTime) {
        this.effectTime = effectTime;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }
}
