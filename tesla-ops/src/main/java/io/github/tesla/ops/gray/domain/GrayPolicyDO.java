package io.github.tesla.ops.gray.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 灰度策略
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:50
 * @version: V1.0.0
 * @since JDK 11
 */
@TableName("gateway_gray_policy")
public class GrayPolicyDO extends BaseDO {

    /**
     * 计划编号
     */
    @TableField("plan_id")
    private Long planId;

    /**
     * 服务消费方
     */
    @TableField("consumer_service")
    private String consumerService;

    /**
     * 服务提供方
     */
    @TableField("provider_service")
    private String providerService;

    public String getConsumerService() {
        return consumerService;
    }

    public Long getPlanId() {
        return planId;
    }

    public String getProviderService() {
        return providerService;
    }

    public void setConsumerService(String consumerService) {
        this.consumerService = consumerService;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public void setProviderService(String providerService) {
        this.providerService = providerService;
    }
}
