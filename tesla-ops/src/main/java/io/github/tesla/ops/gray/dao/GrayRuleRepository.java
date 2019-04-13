package io.github.tesla.ops.gray.dao;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import io.github.tesla.ops.common.TeslaException;
import io.github.tesla.ops.gray.domain.GrayRuleDO;

/**
 * ${todo}
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 16:47
 * @version: V1.0.0
 * @since JDK 11
 */
@Repository
public class GrayRuleRepository {

    private static final Logger log = LoggerFactory.getLogger(GrayRuleRepository.class);

    @Autowired
    private GrayRuleMapper grayRuleMapper;

    public GrayRuleDO load(Long id) {
        return grayRuleMapper.selectById(id);
    }

    public boolean save(GrayRuleDO entity) {
        return grayRuleMapper.insert(entity) == 1;
    }

    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public boolean save(List<GrayRuleDO> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return false;
        }
        // TODO 暂时由单笔插入执行
        for (GrayRuleDO entity : entities) {
            if (!this.save(entity)) {
                log.error("The gray rule save failed:{}", entity);
                throw new TeslaException("The gray rule save failed");
            }
        }
        return true;
    }

    public boolean update(GrayRuleDO entity) {
        if (entity.getId() == null) {
            return false;
        }
        return grayRuleMapper.updateById(entity) == 1;
    }

    public boolean delete(Long id) {
        return grayRuleMapper.deleteById(id) == 1;
    }

    public boolean deleteByPolicy(Long policyId) {
        GrayRuleDO grayRule = this.findByPolicy(policyId);
        if (Objects.isNull(grayRule)) {
            return true;
        }
        return this.delete(grayRule.getId());
    }

    public List<GrayRuleDO> findByPlan(Long planId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("plan_id", planId);
        return grayRuleMapper.selectByMap(param);
    }

    public GrayRuleDO findByPolicy(Long policyId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("policy_id", policyId);
        List<GrayRuleDO> ruleList = grayRuleMapper.selectByMap(param);
        return CollectionUtils.isEmpty(ruleList) ? null : ruleList.get(0);
    }
}
