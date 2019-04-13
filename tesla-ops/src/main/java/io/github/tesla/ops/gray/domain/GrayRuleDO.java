package io.github.tesla.ops.gray.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 灰度规则
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:50
 * @version: V1.0.0
 * @since JDK 11
 */
@TableName("gateway_gray_rule")
public class GrayRuleDO extends BaseDO {

    /**
     * 计划编号
     */
    @TableField("policy_id")
    private Long policyId;

    /**
     * 服务消费方
     */
    @TableField("rule_kind")
    private String ruleKind;

    /**
     * 服务提供方
     */
    @TableField("rule_content")
    private String ruleContent;

    public Long getPolicyId() {
        return policyId;
    }

    public String getRuleContent() {
        return ruleContent;
    }

    public String getRuleKind() {
        return ruleKind;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }

    public void setRuleKind(String ruleKind) {
        this.ruleKind = ruleKind;
    }
}
