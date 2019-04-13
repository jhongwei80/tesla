package io.github.tesla.ops.gray.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import io.github.tesla.ops.gray.domain.GrayPolicyConditionDO;
import io.github.tesla.ops.gray.domain.GrayPolicyDO;
import io.github.tesla.ops.gray.domain.GrayRuleDO;
import io.github.tesla.ops.gray.helper.ServiceTarget;

/**
 * 灰度计划模型
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:50
 * @version: V1.0.0
 * @since JDK 11
 */
public class GrayPolicyDTO {

    private GrayPolicyDO grayPolicy;

    /**
     * 灰度策略条件
     */
    private List<GrayPolicyConditionDO> conditions;

    private GrayRuleDO grayRule;

    public void addCondition(GrayPolicyConditionDO condition) {
        if (this.conditions == null) {
            this.conditions = Lists.newArrayList();
        }
        this.conditions.add(condition);
    }

    public void addCondition(List<GrayPolicyConditionDO> conditions) {
        if (this.conditions == null) {
            this.conditions = Lists.newArrayList();
        }
        this.conditions.addAll(conditions);
    }

    public List<GrayPolicyConditionDO> getConditions() {
        return conditions;
    }

    public List<GrayPolicyConditionDO> getConsumerConditions() {
        return conditions.stream()
            .filter(condition -> ServiceTarget.CONSUMER == ServiceTarget.get(condition.getServiceTarget()))
            .collect(Collectors.toList());
    }

    public GrayPolicyDO getGrayPolicy() {
        return grayPolicy;
    }

    public GrayRuleDO getGrayRule() {
        return grayRule;
    }

    public List<GrayPolicyConditionDO> getProviderConditions() {
        return conditions.stream()
            .filter(condition -> ServiceTarget.PROVIDER == ServiceTarget.get(condition.getServiceTarget()))
            .collect(Collectors.toList());
    }

    public void setConditions(List<GrayPolicyConditionDO> conditions) {
        this.conditions = conditions;
    }

    public void setGrayPolicy(GrayPolicyDO grayPolicy) {
        this.grayPolicy = grayPolicy;
    }

    public void setGrayRule(GrayRuleDO grayRule) {
        this.grayRule = grayRule;
    }
}
