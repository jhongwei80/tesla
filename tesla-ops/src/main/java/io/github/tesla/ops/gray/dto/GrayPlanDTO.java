package io.github.tesla.ops.gray.dto;

import java.util.List;

import com.google.common.collect.Lists;

import io.github.tesla.ops.gray.domain.GrayPlanDO;

/**
 * 灰度计划模型
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:50
 * @version: V1.0.0
 * @since JDK 11
 */
public class GrayPlanDTO {

    private GrayPlanDO grayPlan;

    /**
     * 灰度策略
     */
    private List<GrayPolicyDTO> grayPolicyList;

    public void addGrayPolicy(GrayPolicyDTO model) {
        if (this.grayPolicyList == null) {
            this.grayPolicyList = Lists.newArrayList();
        }
        this.grayPolicyList.add(model);
    }

    public GrayPlanDO getGrayPlan() {
        return grayPlan;
    }

    public List<GrayPolicyDTO> getGrayPolicyList() {
        return grayPolicyList;
    }

    public void setGrayPlan(GrayPlanDO grayPlan) {
        this.grayPlan = grayPlan;
    }

    public void setGrayPolicyList(List<GrayPolicyDTO> grayPolicyList) {
        this.grayPolicyList = grayPolicyList;
    }
}
