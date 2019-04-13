package io.github.tesla.ops.gray.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 灰度策略
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/11/5 17:16
 * @version: V1.0.0
 * @since JDK 11
 */
public class GrayPolicyVO implements Serializable {
    private Long id;

    private Long planId;

    private String consumerService;

    private String providerService;

    private List<GrayPolicyConditionVO> grayPolicyParamConditionVOS;
    private List<GrayPolicyConditionVO> grayPolicyNodeConditionVOS;

    public String getConsumerService() {
        return consumerService;
    }

    public List<GrayPolicyConditionVO> getGrayPolicyNodeConditionVOS() {
        return grayPolicyNodeConditionVOS;
    }

    public List<GrayPolicyConditionVO> getGrayPolicyParamConditionVOS() {
        return grayPolicyParamConditionVOS;
    }

    public Long getId() {
        return id;
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

    public void setGrayPolicyNodeConditionVOS(List<GrayPolicyConditionVO> grayPolicyNodeConditionVOS) {
        this.grayPolicyNodeConditionVOS = grayPolicyNodeConditionVOS;
    }

    public void setGrayPolicyParamConditionVOS(List<GrayPolicyConditionVO> grayPolicyParamConditionVOS) {
        this.grayPolicyParamConditionVOS = grayPolicyParamConditionVOS;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public void setProviderService(String providerService) {
        this.providerService = providerService;
    }
}
