package io.github.tesla.ops.gray.service.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import io.github.tesla.ops.common.BaseResult;
import io.github.tesla.ops.common.Constant;
import io.github.tesla.ops.common.TeslaException;
import io.github.tesla.ops.gray.dao.GrayPlanRepository;
import io.github.tesla.ops.gray.dao.GrayPolicyConditionRepository;
import io.github.tesla.ops.gray.dao.GrayPolicyRepository;
import io.github.tesla.ops.gray.dao.GrayRuleRepository;
import io.github.tesla.ops.gray.domain.GrayPlanDO;
import io.github.tesla.ops.gray.domain.GrayPolicyConditionDO;
import io.github.tesla.ops.gray.domain.GrayPolicyDO;
import io.github.tesla.ops.gray.domain.GrayRuleDO;
import io.github.tesla.ops.gray.dto.GrayPlanDTO;
import io.github.tesla.ops.gray.dto.GrayPolicyDTO;
import io.github.tesla.ops.gray.helper.*;
import io.github.tesla.ops.gray.service.GrayService;
import io.github.tesla.ops.gray.vo.GrayPlanVO;
import io.github.tesla.ops.gray.vo.GrayPolicyConditionVO;
import io.github.tesla.ops.gray.vo.GrayPolicyVO;
import io.github.tesla.ops.utils.DateUtils;
import io.github.tesla.ops.utils.FileUtil;

/**
 * 灰度服务
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:47
 * @version: V1.0.0
 * @since JDK 11
 */
@Service
@Transactional
public class GrayServiceImpl implements GrayService {

    private final static Logger logger = LoggerFactory.getLogger(GrayServiceImpl.class);

    @Autowired
    private GrayPlanRepository grayPlanRepository;

    @Autowired
    private GrayRuleRepository grayRuleRepository;

    @Autowired
    private GrayPolicyRepository grayPolicyRepository;

    @Autowired
    private GrayPolicyConditionRepository grayPolicyConditionRepository;

    @Override
    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public BaseResult addGrayPolicy(Long grayPlanId, GrayPolicyDTO grayPolicy) {
        GrayPlanDO planModel = grayPlanRepository.lightLoad(grayPlanId);
        if (Objects.isNull(planModel)) {
            return BaseResult.failM("The gray plan not exist.");
        }
        grayPolicy.getGrayPolicy().setPlanId(grayPlanId);
        if (!grayPolicyRepository.save(grayPolicy.getGrayPolicy())) {
            return BaseResult.failM("The gray policy create failed.");
        }
        List<GrayPolicyConditionDO> conditions = Lists.newArrayList();
        conditions.addAll(grayPolicy.getConsumerConditions());
        conditions.addAll(grayPolicy.getProviderConditions());
        if (!grayPolicyConditionRepository.save(conditions)) {
            throw new TeslaException("The gray policy condition create failed.");
        }
        return BaseResult.success();
    }

    private List<Edges> buildEdges(GrayPolicyDO policy) {
        Edges grayEdge = new Edges();
        grayEdge.setFrom(policy.getConsumerService());
        grayEdge.setTo(policy.getProviderService());

        grayEdge.setConditions(grayPolicyConditionRepository.findConsumerByPolicy(policy.getId()));

        GrayRuleDO grayRule = grayRuleRepository.findByPolicy(policy.getId());
        grayEdge.setGroovy(Objects.isNull(grayRule) ? null : grayRule.getRuleContent());

        Edges normalEdge = new Edges();
        normalEdge.setFrom(policy.getConsumerService());
        normalEdge.setTo(policy.getProviderService() + "-ngray");

        return Arrays.asList(grayEdge, normalEdge);
    }

    private Node buildGrayNode(Long conditionPolicy, GrayPolicyDO policy, boolean isParent, int level,
        boolean provider) {
        Node node = new Node();
        node.setLabel(policy.getConsumerService());
        node.setId(policy.getConsumerService());
        node.setLevel(level);
        node.setGroup(level);
        node.setParent(isParent);
        node.setGrayNode(true);
        if (provider) {
            node.setConditions(grayPolicyConditionRepository.findProviderByPolicy(conditionPolicy));
        }
        return node;
    }

    private Set<Node> buildLastedNode(GrayPolicyDO policy, int level) {
        Node grayNode = new Node();
        grayNode.setLabel(policy.getProviderService());
        grayNode.setId(policy.getProviderService());
        grayNode.setLevel(level);
        grayNode.setGroup(level);
        grayNode.setParent(false);
        grayNode.setGrayNode(true);
        grayNode.setConditions(grayPolicyConditionRepository.findProviderByPolicy(policy.getId()));

        Node normalNode = new Node();
        normalNode.setLabel(policy.getProviderService());
        normalNode.setId(policy.getProviderService() + "-ngray");
        normalNode.setLevel(level);
        normalNode.setGroup(level);
        normalNode.setParent(false);
        normalNode.setGrayNode(false);

        return Sets.newHashSet(grayNode, normalNode);
    }

    private void buildNodes(Long parentPolicyId, GrayPolicyDO policy, Node parentNode, boolean provider,
        List<GrayPolicyDO> dataSource, Set<Node> nodes, List<Edges> edges) {
        Node grayNode = this.buildGrayNode(parentPolicyId, policy, false, parentNode.getLevel() + 1, provider);
        Node normalNode = this.buildNormalNode(policy, parentNode.getLevel() + 1);
        nodes.add(grayNode);
        nodes.add(normalNode);
        edges.addAll(this.buildEdges(policy));
        GrayPolicyDO nextGrayPolicy = this.findNode(dataSource, policy.getProviderService());
        if (Objects.nonNull(nextGrayPolicy)) {
            this.buildNodes(policy.getId(), nextGrayPolicy, grayNode, true, dataSource, nodes, edges);
        } else {
            nodes.addAll(this.buildLastedNode(policy, grayNode.getLevel() + 1));
        }
    }

    private Node buildNormalNode(GrayPolicyDO policy, int level) {
        Node node = new Node();
        node.setLabel(policy.getConsumerService());
        node.setId(policy.getConsumerService() + "-ngray");
        node.setLevel(level);
        node.setGroup(level);
        node.setGrayNode(false);
        node.setParent(false);
        return node;
    }

    private void buildParentNode(List<GrayPolicyDO> grayPolicyRoots, List<GrayPolicyDO> dataSource, Set<Node> nodes,
        List<Edges> edges) {
        grayPolicyRoots.forEach(policy -> {
            Node grayNode = this.buildGrayNode(policy.getId(), policy, true, 0, false);
            nodes.add(grayNode);
            edges.addAll(this.buildEdges(policy));
            GrayPolicyDO nextGrayPolicy = this.findNode(dataSource, policy.getProviderService());
            if (Objects.nonNull(nextGrayPolicy)) {
                this.buildNodes(policy.getId(), nextGrayPolicy, grayNode, true, dataSource, nodes, edges);
            } else {
                nodes.addAll(this.buildLastedNode(policy, grayNode.getLevel() + 1));
            }
        });
    }

    @Override
    public BaseResult createGrayPlan(GrayPlanDO grayPlan) {
        logger.debug("create gray plan:{}", grayPlan);
        if (Objects.isNull(grayPlan)) {
            return BaseResult.failM("The parameter cannot be empty, please check.");
        }
        if (grayPlanRepository.existed(grayPlan.getPlanName())) {
            return BaseResult.failM("The gray plan name existed.");
        }
        if (Objects.isNull(grayPlan.getGmtCreate())) {
            grayPlan.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        }
        if (Objects.isNull(grayPlan.getGmtModified())) {
            grayPlan.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
        }
        if (!grayPlanRepository.save(grayPlan)) {
            return BaseResult.failM("The gray plan create failed.");
        }
        return BaseResult.success();
    }

    /**
     * 功能描述: 新增或修改grayPlan,grayPolicy,grayPolicyCondition
     * 
     * @parmname: createGrayPlan
     * @param: [grayPlan]
     * @return: io.github.tesla.ops.common.BaseResult
     * @auther: zhipingzhang
     * @date: 2018/11/7 16:07
     */
    @Override
    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public BaseResult createGrayPlan(GrayPlanVO grayPlan) {
        if (grayPlan.getId() == null && org.apache.commons.lang3.StringUtils.isEmpty(grayPlan.getEnable())) {
            grayPlan.setEnable(YesNoKind.NO.getCode());
        }
        GrayPlanDO model = grayPlan.createGrayPlanDO();
        BaseResult result = BaseResult.success();
        if (model.getId() == null) {
            model.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
            model.setGmtModified(model.getGmtCreate());
            result = createGrayPlan(model);
            grayPlan.setId(model.getId());
        } else {
            model.setGmtModified(Timestamp.valueOf(LocalDateTime.now()));
            if (!grayPlanRepository.update(model)) {
                result = BaseResult.fail();
                return result;
            }
        }
        GrayPolicyDO grayPolicyDO = grayPlan.createGrayPolicyDO();
        if (Objects.isNull(grayPolicyDO)) {
            return result;
        }
        if (grayPolicyDO.getConsumerService().equals(grayPolicyDO.getProviderService())) {
            return BaseResult.failM("The consumer is same as provider service");
        }
        if (grayPolicyDO != null && grayPolicyDO.getId() == null) {
            if (!grayPolicyRepository.save(grayPolicyDO)) {
                result = BaseResult.fail();
            }
        } else if (grayPolicyDO != null && grayPolicyDO.getId() != null) {
            if (!grayPolicyRepository.update(grayPolicyDO)) {
                result = BaseResult.fail();
            }
        }
        result.getData().put("grayPolicyId", grayPolicyDO.getId());
        List<GrayPolicyConditionDO> grayPolicyConditionDOList =
            grayPlan.createGrayPolicyConditionDOList(grayPolicyDO.getId());
        if (!grayPolicyConditionRepository.deletByPolicyId(grayPolicyDO.getId())) {
            result = BaseResult.fail();
        }
        if (!grayPolicyConditionRepository.save(grayPolicyConditionDOList)) {
            result = BaseResult.fail();
        }

        // 更新规则脚本
        GrayRuleDO orgiRuleDO = grayRuleRepository.findByPolicy(grayPolicyDO.getId());
        GrayRuleDO grayRule = this.generateGrayRule(grayPolicyDO, grayPolicyConditionDOList);
        if (Objects.isNull(orgiRuleDO)) {
            if (!grayRuleRepository.save(grayRule)) {
                result = BaseResult.fail();
            }
        } else {
            grayRule.setId(orgiRuleDO.getId());
            grayRule.setGmtCreate(orgiRuleDO.getGmtCreate());
            if (!grayRuleRepository.update(grayRule)) {
                result = BaseResult.fail();
            }
        }
        if (!result.isSuccess()) {
            throw new TeslaException("save grayRule is fail");
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public BaseResult createIntegration(GrayPlanDTO grayPlanDTO) {
        logger.debug("integration create gray plan:{}", grayPlanDTO);
        if (Objects.isNull(grayPlanDTO) || Objects.isNull(grayPlanDTO.getGrayPlan())) {
            return BaseResult.failM("The parameter cannot be empty, please check.");
        }
        GrayPlanDO grayPlan = grayPlanDTO.getGrayPlan();
        if (org.apache.commons.lang3.StringUtils.isEmpty(grayPlan.getEnable())) {
            grayPlan.setEnable(YesNoKind.NO.getCode());
        }
        if (grayPlanRepository.existed(grayPlan.getPlanName())) {
            return BaseResult.failM("The gray plan name existed.");
        }
        if (!grayPlanRepository.store(grayPlanDTO)) {
            return BaseResult.failM("The gray plan create failed.");
        }
        return BaseResult.success();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public BaseResult deleteGrayCascade(Long gratPlanId) {
        grayPlanRepository.delete(gratPlanId);
        List<GrayPolicyDO> grayPolicyList = grayPolicyRepository.findByPlan(gratPlanId);
        if (CollectionUtils.isEmpty(grayPolicyList)) {
            return BaseResult.success();
        }
        grayPolicyList.stream().forEach(policy -> {
            grayPolicyRepository.delete(policy.getId());
            grayPolicyConditionRepository.deletByPolicyId(policy.getId());
            grayRuleRepository.deleteByPolicy(policy.getId());
        });
        return BaseResult.success();
    }

    /**
     * 功能描述: 删除garyPoliy及对应的grayPolicyCondition
     * 
     * @parmname: deletPolicy
     * @param: [id]
     * @return: io.github.tesla.ops.common.BaseResult
     * @auther: zhipingzhang
     * @date: 2018/11/7 16:08
     */
    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    @Override
    public BaseResult deletPolicy(Long id) {
        if (!grayPolicyRepository.delete(id)) {
            return BaseResult.failM("delete grayPolicy is fail");
        }
        if (!grayPolicyConditionRepository.deletByPolicyId(id)) {
            throw new TeslaException("delete grayPolicy is fail");
        }
        if (!grayRuleRepository.deleteByPolicy(id)) {
            throw new TeslaException("delete grayPolicy rule is fail");
        }
        return BaseResult.success();
    }

    @Override
    public BaseResult editGrayPlan(GrayPlanDO grayPlan) {
        if (grayPlan.getId() == null) {
            return BaseResult.fail();
        }
        return grayPlanRepository.update(grayPlan) ? BaseResult.success() : BaseResult.fail();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public BaseResult editGrayPolicy(GrayPolicyDTO grayPolicyDto) {
        GrayPolicyDO grayPolicy = grayPolicyDto.getGrayPolicy();
        if (grayPolicy.getId() == null) {
            return BaseResult.fail();
        }
        if (!grayPolicyRepository.update(grayPolicy)) {
            return BaseResult.fail();
        }
        List<GrayPolicyConditionDO> conditions = Lists.newArrayList();
        conditions.addAll(grayPolicyDto.getConsumerConditions());
        conditions.addAll(grayPolicyDto.getProviderConditions());
        if (!grayPolicyConditionRepository.update(conditions)) {
            throw new TeslaException("The gray policy condition update failed.");
        }
        GrayRuleDO orgiGrayRule = grayRuleRepository.findByPolicy(grayPolicy.getId());
        GrayRuleDO grayRule = this.generateGrayRule(grayPolicy, conditions);
        if (Objects.nonNull(orgiGrayRule)) {
            grayRule.setId(orgiGrayRule.getId());
            grayRule.setGmtCreate(orgiGrayRule.getGmtCreate());
        }
        if (!grayRuleRepository.update(grayRule)) {
            throw new TeslaException("The gray policy edit failed, because the gray rule generate failed.");
        }
        return BaseResult.success();
    }

    private GrayPolicyDO findNode(List<GrayPolicyDO> dataSource, String consumerService) {
        for (GrayPolicyDO policy : dataSource) {
            if (policy.getConsumerService().equals(consumerService)) {
                return policy;
            }
        }
        return null;
    }

    @Override
    public Pair<Set<Node>, List<Edges>> findNodes(Long grayPlanId) {
        Set<Node> nodes = Sets.newHashSet();
        List<Edges> edges = Lists.newArrayList();

        List<GrayPolicyDO> grayPolicyRoots = grayPolicyRepository.findRootNodes(grayPlanId);
        if (CollectionUtils.isEmpty(grayPolicyRoots)) {
            return Pair.of(nodes, edges);
        }
        List<GrayPolicyDO> dataSource = grayPolicyRepository.findByPlan(grayPlanId);
        this.buildParentNode(grayPolicyRoots, dataSource, nodes, edges);

        return Pair.of(nodes, edges);
    }

    /**
     * 生成灰度策略执行脚本
     * 
     * @param grayPolicy
     */
    private GrayRuleDO generateGrayRule(GrayPolicyDO grayPolicy) {
        List<GrayPolicyConditionDO> conditions = grayPolicyConditionRepository.findByPolicy(grayPolicy.getId());
        return this.generateGrayRule(grayPolicy, conditions);
    }

    private GrayRuleDO generateGrayRule(GrayPolicyDO grayPolicy, List<GrayPolicyConditionDO> conditions) {
        String path = "/META-INF/config/gray_template.ftl";
        String grayFreemarkerTemplate = FileUtil.readTextFromFile(path);
        logger.debug("generate gray rule, template:{}", grayFreemarkerTemplate);
        if (org.apache.commons.lang3.StringUtils.isEmpty(grayFreemarkerTemplate)) {
            throw new TeslaException("gray rule ftl template is empty.");
        }
        List<GrayPolicyConditionDO> consumerConditions = conditions.stream()
            .filter(condition -> ServiceTarget.get(condition.getServiceTarget()) == ServiceTarget.CONSUMER)
            .collect(Collectors.toList());
        List<GrayPolicyConditionDO> providerConditions = conditions.stream()
            .filter(condition -> ServiceTarget.get(condition.getServiceTarget()) == ServiceTarget.PROVIDER)
            .collect(Collectors.toList());
        Map<String, Object> param = Maps.newHashMapWithExpectedSize(2);
        param.put(Constant.GRAY_TEMPLATE_PARAM_KEY_CONDITIONS, consumerConditions);
        param.put(Constant.GRAY_TEMPLATE_PARAM_KEY_NODES, providerConditions);
        GrayRuleDO model = new GrayRuleDO();
        model.setPolicyId(grayPolicy.getId());
        model.setRuleKind(Constant.GRAY_RULE_TEMPLATE_FTL);
        model.setRuleContent(FreemarkerHelper.generate(this.getTemplateId(grayPolicy), grayFreemarkerTemplate, param));
        model.setGmtCreate(Timestamp.valueOf(LocalDateTime.now()));
        model.setGmtModified(model.getGmtCreate());
        return model;
    }

    @Override
    public GrayPlanVO getGrayPlanVOFormDB(Long id) {

        GrayPlanDO grayPlanDO = grayPlanRepository.lightLoad(id);

        GrayPlanVO grayPlanVO = BeanMapper.map(grayPlanDO, GrayPlanVO.class);
        grayPlanVO.setEffectTime(DateUtils.format(grayPlanDO.getEffectTime(), DateUtils.DATE_TIME_PATTERN));
        grayPlanVO.setExpireTime(DateUtils.format(grayPlanDO.getExpireTime(), DateUtils.DATE_TIME_PATTERN));
        List<GrayPolicyVO> grayPolicyVOS = Lists.newArrayList();
        List<GrayPolicyConditionVO> grayPolicyParamConditionVOS;
        List<GrayPolicyConditionVO> grayPolicyNodeConditionVOS;
        List<GrayPolicyDO> byPlan = grayPolicyRepository.findByPlan(id);
        if (byPlan != null && !byPlan.isEmpty()) {
            for (GrayPolicyDO grayPolicyDO : byPlan) {
                GrayPolicyVO grayPolicyVO = BeanMapper.map(grayPolicyDO, GrayPolicyVO.class);
                grayPolicyParamConditionVOS = Lists.newArrayList();
                grayPolicyNodeConditionVOS = Lists.newArrayList();
                List<GrayPolicyConditionDO> byPolicy = grayPolicyConditionRepository.findByPolicy(grayPolicyDO.getId());
                if (byPolicy != null && !byPolicy.isEmpty()) {
                    for (GrayPolicyConditionDO grayPolicyConditionDO : byPolicy) {
                        GrayPolicyConditionVO grayPolicyConditionVO =
                            BeanMapper.map(grayPolicyConditionDO, GrayPolicyConditionVO.class);
                        if (grayPolicyConditionVO.getParamKind().equalsIgnoreCase(GrayParamKind.NODE.getCode())) {
                            grayPolicyNodeConditionVOS.add(grayPolicyConditionVO);
                        } else {
                            grayPolicyParamConditionVOS.add(grayPolicyConditionVO);
                        }
                    }
                }
                grayPolicyVO.setGrayPolicyNodeConditionVOS(grayPolicyNodeConditionVOS);
                grayPolicyVO.setGrayPolicyParamConditionVOS(grayPolicyParamConditionVOS);
                grayPolicyVOS.add(grayPolicyVO);
            }
        }
        grayPlanVO.setGrayPolicyVOS(grayPolicyVOS);
        return grayPlanVO;
    }

    private String getTemplateId(GrayPolicyDO policy) {
        return policy.getPlanId() + "-" + policy.getId() + "-" + policy.getConsumerService() + "-"
            + policy.getProviderService();
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, TeslaException.class})
    public BaseResult pushGrayPlan(Long grayPlanId) {
        logger.info("push gray policy:{}", grayPlanId);
        GrayPlanDO grayPlan = grayPlanRepository.lightLoad(grayPlanId);
        if (Objects.isNull(grayPlan)) {
            return BaseResult.failM("The gray plan not exist");
        }
        // 校验有效期
        Timestamp nowTime = Timestamp.valueOf(LocalDateTime.now());
        if (nowTime.before(grayPlan.getEffectTime()) || nowTime.after(grayPlan.getExpireTime())) {
            return BaseResult.failM("The gray plan is dead");
        }
        if (YesNoKind.NO == YesNoKind.get(grayPlan.getEnable())) {
            grayPlanRepository.updateEnbale(grayPlan.getId(), YesNoKind.YES, YesNoKind.NO);
        }

        // 检查策略脚本
        List<GrayPolicyDO> policyList = grayPolicyRepository.findByPlan(grayPlanId);
        if (CollectionUtils.isEmpty(policyList)) {
            return BaseResult.failM("The gray plan policy not exist");
        }
        for (GrayPolicyDO policy : policyList) {
            GrayRuleDO grayRule = grayRuleRepository.findByPolicy(policy.getId());
            if (Objects.isNull(grayRule)) {
                logger.debug("gray policy rule not exist, and generate now");
                grayRule = this.generateGrayRule(policy);
                if (!grayRuleRepository.save(grayRule)) {
                    return BaseResult.failM("The gray policy push failed, because the gray rule generate failed.");
                }
            }
        }
        return BaseResult.success();
    }

}
