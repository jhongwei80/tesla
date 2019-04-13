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

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import io.github.tesla.ops.common.TeslaException;
import io.github.tesla.ops.gray.domain.GrayPlanDO;
import io.github.tesla.ops.gray.domain.GrayPolicyConditionDO;
import io.github.tesla.ops.gray.dto.GrayPlanDTO;
import io.github.tesla.ops.gray.dto.GrayPolicyDTO;
import io.github.tesla.ops.gray.helper.YesNoKind;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;

/**
 * ${todo}
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 16:08
 * @version: V1.0.0
 * @since JDK 11
 */
@Repository
public class GrayPlanRepository {

    private final static Logger logger = LoggerFactory.getLogger(GrayPlanRepository.class);

    @Autowired
    private GrayPlanMapper grayPlanMapper;

    @Autowired
    private GrayPolicyRepository grayPolicyRepository;

    @Autowired
    private GrayPolicyConditionRepository grayPolicyConditionRepository;

    public boolean existed(String planName) {
        return grayPlanMapper.selectCount(Wrappers.<GrayPlanDO>query().eq("plan_name", planName)) > 0;
    }

    public GrayPlanDO lightLoad(Long id) {
        return grayPlanMapper.selectById(id);
    }

    public PageDO<GrayPlanDO> findByPage(Query query) {
        int pageNo = Integer.parseInt(String.valueOf(query.remove("page")));
        int pageSize = Integer.parseInt(String.valueOf(query.remove("limit")));
        query.remove("offset");
        IPage iPage = grayPlanMapper.selectPage(new Page(pageNo, pageSize), Wrappers.<GrayPlanDO>query().allEq(query));
        PageDO<GrayPlanDO> pageInfo = new PageDO<>();
        pageInfo.setRows(iPage.getRecords());
        pageInfo.setTotal((int)iPage.getTotal());
        query.setLimit(pageSize);
        query.setOffset(pageNo);
        pageInfo.setParams(query);
        return pageInfo;
    }

    public boolean save(GrayPlanDO entity) {
        return grayPlanMapper.insert(entity) == 1;
    }

    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public boolean store(GrayPlanDTO grayPlan) throws TeslaException {
        if (Objects.isNull(grayPlan)) {
            return false;
        }
        // 存储计划
        GrayPlanDO grayPlanDO = grayPlan.getGrayPlan();
        if (Objects.isNull(grayPlanDO.getGmtCreate())) {
            grayPlanDO.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        }
        if (Objects.isNull(grayPlanDO.getGmtModified())) {
            grayPlanDO.setGmtModified(grayPlanDO.getGmtCreate());
        }
        if (!this.save(grayPlanDO)) {
            logger.info("The gray plan save failed:{}", grayPlanDO);
            return false;
        }
        List<GrayPolicyDTO> grayPolicyDTOS = grayPlan.getGrayPolicyList();
        if (CollectionUtils.isEmpty(grayPolicyDTOS)) {
            return false;
        }

        // 存储策略
        grayPolicyDTOS.forEach(policy -> policy.getGrayPolicy().setPlanId(grayPlanDO.getId()));
        if (!grayPolicyRepository.save(grayPolicyDTOS)) {
            logger.info("The gray policy save failed:{}", grayPolicyDTOS);
            throw new TeslaException("The gray policy save failed.");
        }

        // 存储策略条件
        List<GrayPolicyConditionDO> gpc = Lists.newArrayList();
        grayPolicyDTOS.forEach(policy -> {
            policy.getConsumerConditions().forEach(cpc -> {
                cpc.setPlanId(policy.getGrayPolicy().getPlanId());
                cpc.setPolicyId(policy.getGrayPolicy().getId());
                gpc.add(cpc);
            });
            policy.getProviderConditions().forEach(ppc -> {
                ppc.setPlanId(policy.getGrayPolicy().getPlanId());
                ppc.setPolicyId(policy.getGrayPolicy().getId());
                gpc.add(ppc);
            });
        });

        if (!grayPolicyConditionRepository.save(gpc)) {
            logger.info("The gray policy condition save failed:{}", gpc);
            throw new TeslaException("The gray policy condition save failed.");
        }
        return true;
    }

    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public boolean update(GrayPlanDO model) {
        if (model.getId() == null) {
            return false;
        }
        return grayPlanMapper.updateById(model) == 1;
    }

    public boolean updateEnbale(Long id, YesNoKind newEnable, YesNoKind oldEnable) {
        GrayPlanDO entity = new GrayPlanDO();
        entity.setId(id);
        entity.setEnable(newEnable.getCode());
        return grayPlanMapper.updateById(entity) == 1;
    }

    public boolean delete(Long id) {
        return grayPlanMapper.deleteById(id) == 1;
    }

    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public boolean delete(Long[] ids) {
        for (Long id : ids) {
            this.delete(id);
        }
        return true;
    }

    public List<GrayPlanDO> selectByParams(Map<String, Object> param) {
        return grayPlanMapper.selectByMap(param);
    }

}
