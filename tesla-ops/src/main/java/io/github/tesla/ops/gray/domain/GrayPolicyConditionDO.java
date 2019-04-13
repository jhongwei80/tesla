package io.github.tesla.ops.gray.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 灰度策略条件
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:50
 * @version: V1.0.0
 * @since JDK 11
 */
@TableName("gateway_gray_policy_condition")
public class GrayPolicyConditionDO extends BaseDO {

    /**
     * 计划编号
     */
    @TableField("plan_id")
    private Long planId;

    /**
     * 策略编号
     */
    @TableField("policy_id")
    private Long policyId;

    /**
     * 服务目标:CONSUMER-消费方;PROVIDER-提供方
     */
    @TableField("service_target")
    private String serviceTarget;

    /**
     * 参数类型:
     */
    @TableField("param_kind")
    private String paramKind;

    /**
     * 参数键
     */
    @TableField("param_key")
    private String paramKey;

    /**
     * 参数值
     */
    @TableField("param_value")
    private String paramValue;

    /**
     * 参数描述
     */
    @TableField("param_desc")
    private String paramDesc;

    /**
     * 是否透传
     */
    @TableField("transmit")
    private String transmit;

    public String getParamDesc() {
        return paramDesc;
    }

    public String getParamKey() {
        return paramKey;
    }

    public String getParamKind() {
        return paramKind;
    }

    public String getParamValue() {
        return paramValue;
    }

    public Long getPlanId() {
        return planId;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public String getServiceTarget() {
        return serviceTarget;
    }

    public String getTransmit() {
        return transmit;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public void setParamKind(String paramKind) {
        this.paramKind = paramKind;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public void setServiceTarget(String serviceTarget) {
        this.serviceTarget = serviceTarget;
    }

    public void setTransmit(String transmit) {
        this.transmit = transmit;
    }
}
