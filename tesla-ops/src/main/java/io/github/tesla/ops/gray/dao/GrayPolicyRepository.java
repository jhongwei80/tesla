package io.github.tesla.ops.gray.dao;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.tesla.ops.common.TeslaException;
import io.github.tesla.ops.gray.domain.GrayPolicyDO;
import io.github.tesla.ops.gray.dto.GrayPolicyDTO;

/**
 * ${todo}
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 16:12
 * @version: V1.0.0
 * @since JDK 11
 */
@Repository
public class GrayPolicyRepository {

    private static final Logger log = LoggerFactory.getLogger(GrayPolicyRepository.class);

    @Autowired
    private GrayPolicyMapper grayPolicyMapper;

    @Autowired
    private GrayPolicyConditionRepository grayPolicyConditionRepository;

    @Autowired
    private GrayRuleRepository grayRuleRepository;

    public GrayPolicyDO load(Long id) {
        return grayPolicyMapper.selectById(id);
    }

    public List<GrayPolicyDTO> deepLoad1(Long grayPlanId) {
        List<GrayPolicyDO> grayPolicyDOS = this.findByPlan(grayPlanId);
        if (CollectionUtils.isEmpty(grayPolicyDOS)) {
            return Lists.newArrayList();
        }
        List<GrayPolicyDTO> grayPolicyDTOS = Lists.newArrayList();
        grayPolicyDOS.forEach(policy -> {
            GrayPolicyDTO grayPolicyDTO = new GrayPolicyDTO();
            grayPolicyDTO.setGrayPolicy(policy);
            grayPolicyDTO.setConditions(grayPolicyConditionRepository.findByPolicy(policy.getId()));
            grayPolicyDTO.setGrayRule(grayRuleRepository.findByPolicy(policy.getId()));
            grayPolicyDTOS.add(grayPolicyDTO);
        });
        return grayPolicyDTOS;
    }

    public GrayPolicyDTO deepLoad(Long grayPolicyId) {
        GrayPolicyDO grayPolicyDO = this.load(grayPolicyId);
        if (Objects.isNull(grayPolicyDO)) {
            return null;
        }
        GrayPolicyDTO grayPolicyDTO = new GrayPolicyDTO();
        grayPolicyDTO.setGrayPolicy(grayPolicyDO);
        grayPolicyDTO.setConditions(grayPolicyConditionRepository.findByPolicy(grayPolicyDO.getId()));
        grayPolicyDTO.setGrayRule(grayRuleRepository.findByPolicy(grayPolicyDO.getId()));
        return grayPolicyDTO;
    }

    public List<GrayPolicyDO> findByPlan(Long grayPlanId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("plan_id", grayPlanId);
        return grayPolicyMapper.selectByMap(param);
    }

    public GrayPolicyDO findByPlan(Long grayPlanId, String consumerService, String providerService) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("plan_id", grayPlanId);
        param.put("consumer_service", consumerService);
        param.put("provider_service", providerService);
        List<GrayPolicyDO> list = grayPolicyMapper.selectByMap(param);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.get(0);
    }

    public boolean save(GrayPolicyDO entity) {
        if (Objects.isNull(entity.getGmtCreate())) {
            entity.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        }
        if (Objects.isNull(entity.getGmtModified())) {
            entity.setGmtModified(entity.getGmtCreate());
        }
        return grayPolicyMapper.insert(entity) == 1;
    }

    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public boolean save(List<GrayPolicyDTO> grayPolicyDTOS) throws TeslaException {
        if (CollectionUtils.isEmpty(grayPolicyDTOS)) {
            return false;
        }
        // TODO 暂时由单笔插入执行
        for (GrayPolicyDTO grayPolicy : grayPolicyDTOS) {
            if (!this.save(grayPolicy.getGrayPolicy())) {
                log.error("The gray policy save failed:{}", grayPolicy.getGrayPolicy());
                throw new TeslaException("The gray policy save failed");
            }
        }
        return true;
    }

    public boolean update(GrayPolicyDO entity) {
        if (entity.getId() == null) {
            return false;
        }
        return grayPolicyMapper.updateById(entity) == 1;
    }

    public boolean delete(Long id) {
        return grayPolicyMapper.deleteById(id) == 1;
    }

    public List<GrayPolicyDO> findRootNodes(Long planId) {
        return grayPolicyMapper.findRootNodes(planId);
    }

}
