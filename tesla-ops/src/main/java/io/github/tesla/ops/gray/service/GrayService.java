package io.github.tesla.ops.gray.service;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import io.github.tesla.ops.common.BaseResult;
import io.github.tesla.ops.gray.domain.GrayPlanDO;
import io.github.tesla.ops.gray.dto.GrayPlanDTO;
import io.github.tesla.ops.gray.dto.GrayPolicyDTO;
import io.github.tesla.ops.gray.helper.Edges;
import io.github.tesla.ops.gray.helper.Node;
import io.github.tesla.ops.gray.vo.GrayPlanVO;

/**
 * 灰度服务
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:46
 * @version: V1.0.0
 * @since JDK 11
 */
public interface GrayService {

    /**
     * 添加灰度策略
     * 
     * @param grayPlanId
     * @param policyModel
     * @return
     */
    BaseResult addGrayPolicy(Long grayPlanId, GrayPolicyDTO policyModel);

    /**
     * 创建灰度计划
     * 
     * @param grayPlan
     * @return
     */
    BaseResult createGrayPlan(GrayPlanDO grayPlan);

    BaseResult createGrayPlan(GrayPlanVO grayPlanVO);

    /**
     * 集成创建灰度计划
     * 
     * @param grayPlan
     * @return
     */
    BaseResult createIntegration(GrayPlanDTO grayPlan);

    /**
     * 级联删除灰度计划
     * 
     * @param gratPlanId
     * @return
     */
    BaseResult deleteGrayCascade(Long gratPlanId);

    BaseResult deletPolicy(Long id);

    /**
     * 更新灰度计划
     * 
     * @return
     */
    BaseResult editGrayPlan(GrayPlanDO grayPlan);

    /**
     * 更新灰度策略
     * 
     * @return
     */
    BaseResult editGrayPolicy(GrayPolicyDTO grayPolicyDto);

    Pair<Set<Node>, List<Edges>> findNodes(Long grayPlanId);

    GrayPlanVO getGrayPlanVOFormDB(Long id);

    /**
     * 按照灰度计划推送策略
     * 
     * @param grayPlanId
     * @return
     */
    BaseResult pushGrayPlan(Long grayPlanId);
}
