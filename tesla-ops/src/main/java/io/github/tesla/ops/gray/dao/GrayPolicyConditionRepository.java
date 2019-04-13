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

import com.google.common.collect.Maps;

import io.github.tesla.ops.common.TeslaException;
import io.github.tesla.ops.gray.domain.GrayPolicyConditionDO;
import io.github.tesla.ops.gray.helper.ServiceTarget;

/**
 * ${todo}
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 16:11
 * @version: V1.0.0
 * @since JDK 11
 */
@Repository
public class GrayPolicyConditionRepository {

    private static final Logger log = LoggerFactory.getLogger(GrayPolicyConditionRepository.class);

    @Autowired
    private GrayPolicyConditionMapper grayPolicyConditionMapper;

    public GrayPolicyConditionDO load(Long id) {
        return grayPolicyConditionMapper.selectById(id);
    }

    public List<GrayPolicyConditionDO> findByPolicy(Long policyId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("policy_id", policyId);
        return grayPolicyConditionMapper.selectByMap(param);
    }

    public List<GrayPolicyConditionDO> findConsumerByPolicy(Long policyId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("policy_id", policyId);
        param.put("service_target", ServiceTarget.CONSUMER.getCode());
        return grayPolicyConditionMapper.selectByMap(param);
    }

    public List<GrayPolicyConditionDO> findProviderByPolicy(Long policyId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("policy_id", policyId);
        param.put("service_target", ServiceTarget.PROVIDER.getCode());
        return grayPolicyConditionMapper.selectByMap(param);
    }

    public boolean save(GrayPolicyConditionDO entity) {
        if (Objects.isNull(entity.getGmtCreate())) {
            entity.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        }
        if (Objects.isNull(entity.getGmtModified())) {
            entity.setGmtModified(entity.getGmtCreate());
        }
        return grayPolicyConditionMapper.insert(entity) == 1;
    }

    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public boolean save(List<GrayPolicyConditionDO> entities) throws TeslaException {
        if (CollectionUtils.isEmpty(entities)) {
            return false;
        }
        // TODO 暂时由单笔插入执行
        for (GrayPolicyConditionDO entity : entities) {
            if (!this.save(entity)) {
                log.error("The gray policy condition save failed:{}", entities);
                throw new TeslaException("The gray policy condition save failed");
            }
        }
        return true;
    }

    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public boolean update(List<GrayPolicyConditionDO> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return false;
        }
        // TODO 暂时由单笔插入执行
        for (GrayPolicyConditionDO entity : entities) {
            if (!this.update(entity)) {
                log.error("The gray policy condition update failed:{}", entities);
                throw new TeslaException("The gray policy condition update failed");
            }
        }
        return true;
    }

    public boolean update(GrayPolicyConditionDO entity) {
        if (entity.getId() == null) {
            return false;
        }
        return grayPolicyConditionMapper.updateById(entity) == 1;
    }

    public boolean delete(Long id) {
        return grayPolicyConditionMapper.deleteById(id) == 1;
    }

    public boolean deletByPolicyId(Long policyId) {
        List<GrayPolicyConditionDO> grayPolicyConditionDOList = findByPolicy(policyId);
        if (grayPolicyConditionDOList == null || grayPolicyConditionDOList.size() == 0) {
            return true;
        }
        for (GrayPolicyConditionDO grayPolicyConditionDO : grayPolicyConditionDOList) {
            if (grayPolicyConditionMapper.deleteById(grayPolicyConditionDO.getId()) != 1) {
                return false;
            }
        }
        return true;
    }
}
