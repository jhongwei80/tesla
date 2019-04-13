package io.github.tesla.ops.gray.schedule;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.*;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.ops.common.AbstractSchedule;
import io.github.tesla.ops.common.MultiDataSourceSwitcher;
import io.github.tesla.ops.common.MultiEurekaServiceSwitcher;
import io.github.tesla.ops.gray.dao.GrayPlanRepository;
import io.github.tesla.ops.gray.dao.GrayPolicyConditionRepository;
import io.github.tesla.ops.gray.dao.GrayPolicyRepository;
import io.github.tesla.ops.gray.dao.GrayRuleRepository;
import io.github.tesla.ops.gray.domain.GrayPlanDO;
import io.github.tesla.ops.gray.domain.GrayPolicyConditionDO;
import io.github.tesla.ops.gray.domain.GrayPolicyDO;
import io.github.tesla.ops.gray.domain.GrayRuleDO;
import io.github.tesla.ops.gray.helper.GrayParamKind;
import io.github.tesla.ops.gray.helper.YesNoKind;
import io.github.tesla.ops.utils.EurekaClientUtil;
import io.github.tesla.ops.utils.MD5Utils;

/**
 * @author: zhipingzhang
 * @date: 2018/11/5 17:28
 * @description: 推送灰度计划
 */
@Component
@DependsOn({"multiEurekaSwitcher", "multiDataSourceSwitcher"})
public class GrayPushSchedule extends AbstractSchedule {

    private String grayEnable = "gray.enable";
    private String grayUrl = "management.url";

    private RestTemplate restTemplate;

    @Autowired
    private GrayPlanRepository grayPlanRepository;
    @Autowired
    private GrayPolicyRepository grayPolicyRepository;
    @Autowired
    private GrayRuleRepository grayRuleRepository;
    @Autowired
    private GrayPolicyConditionRepository grayPolicyConditionRepository;

    @Override
    protected void init() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(2000);
        requestFactory.setReadTimeout(2000);
        restTemplate = new RestTemplate(requestFactory);
    }

    @Override
    protected void doCache() {
        LOGGER.info("begin push gray rule");
        MultiDataSourceSwitcher.getMultiDataSourceNames().keySet().forEach(dataSourceKey -> {
            MultiDataSourceSwitcher.setDataSourceKey(dataSourceKey);
            pushGrayRule(MultiEurekaServiceSwitcher.getEurekaServiceUrl(dataSourceKey));
        });
        LOGGER.info("end push gray rule");
    }

    private void pushGrayRule(String eurekaUrl) {
        if (StringUtils.isBlank(eurekaUrl)) {
            return;
        }
        Map<String, Map<String, Object>> grayPlanMap = creatGrayPlanMap();
        Map<String, String> grayHashMap = Maps.newHashMap();
        if (grayPlanMap != null && !grayPlanMap.isEmpty()) {
            // 计算每一个appd对应的hash值
            for (String consumerServiceName : grayPlanMap.keySet()) {
                grayHashMap.put(consumerServiceName,
                    MD5Utils.jdkSHA1(JsonUtils.serializeToJson(grayPlanMap.get(consumerServiceName))));
            }
        }
        // 读取euerka列表
        EurekaClientUtil.getApplications(eurekaUrl).getRegisteredApplications().forEach(app -> {
            app.getInstances().parallelStream()
                .filter(instance -> MapUtils.getBooleanValue(instance.getMetadata(), grayEnable)
                    && isNotBlank(MapUtils.getString(instance.getMetadata(), grayUrl)))
                .forEach(instanceInfo -> {
                    String grayManagementUrl = null;
                    try {
                        grayManagementUrl = MapUtils.getString(instanceInfo.getMetadata(), grayUrl);
                        String hash = MapUtils.getString(
                            restTemplate.getForObject(grayManagementUrl + "/gray/rules/hash", HashMap.class), "hash");
                        LOGGER.info("eureka instance {}-{} hash :{}", instanceInfo.getAppName(),
                            instanceInfo.getIPAddr(), hash);
                        LOGGER.info("local {} grayHash :{}", instanceInfo.getAppName(),
                            grayHashMap.get(instanceInfo.getAppName()));
                        Map<String, Object> pushRuleMap = Maps.newHashMap();
                        if (StringUtils.isEmpty(grayHashMap.get(instanceInfo.getAppName())) && !"".equals(hash)) {
                            pushRuleMap.put("hash", "");
                            pushRuleMap.put("grayRules", Maps.newHashMap());
                        } else if (StringUtils.isNotEmpty(grayHashMap.get(instanceInfo.getAppName()))
                            && (StringUtils.isBlank(hash)
                                || !grayHashMap.get(instanceInfo.getAppName()).equalsIgnoreCase(hash))) {
                            // 重新推送
                            pushRuleMap.put("hash", grayHashMap.get(instanceInfo.getAppName()));
                            pushRuleMap.put("grayRules", grayPlanMap.get(instanceInfo.getAppName()));
                        } else {
                            return;
                        }
                        HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
                        HttpEntity<String> entity = new HttpEntity<>(JsonUtils.serializeToJson(pushRuleMap), headers);
                        restTemplate.put(grayManagementUrl + "/gray/rules", entity);
                    } catch (RestClientException e) {
                        LOGGER.warn("push gray rule to {} failed , cause by {}", grayManagementUrl, e.getMessage());
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                });
        });
    }

    /**
     * 功能描述: 构造用于推送规则的数据
     *
     * @parmname: creatGrayPlanMap
     * @param: []
     * @return: java.util.Map<java.lang.String , java.util.Map < java.lang.String , java.lang.Object>>
     * @auther: zhipingzhang
     * @date: 2018/11/6 14:05
     */
    private Map<String, Map<String, Object>> creatGrayPlanMap() {

        Map<String, Object> planParamMap = Maps.newHashMap();
        planParamMap.put("enable", YesNoKind.YES.getCode());
        Date dateNow = new Date();
        Map<String, Map<String, Object>> grayPlanMap = Maps.newHashMap();
        List<GrayPlanDO> grayPlanDOS = grayPlanRepository.selectByParams(planParamMap);

        if (grayPlanDOS == null || grayPlanDOS.size() == 0) {
            LOGGER.info("grayPlan is empty return");
            return null;
        }
        grayPlanDOS.stream().filter(grayPlanDO -> grayPlanDO.getEffectTime().compareTo(dateNow) < 0
            && grayPlanDO.getExpireTime().compareTo(dateNow) > 0).forEach(grayPlanDO -> {

                List<GrayPolicyDO> grayPolicyDOS = grayPolicyRepository.findByPlan(grayPlanDO.getId());
                if (grayPolicyDOS == null || grayPolicyDOS.size() == 0) {
                    return;
                }
                grayPolicyDOS.stream().forEach(grayPolicyDO -> {
                    List<GrayPolicyConditionDO> grayPolicyConditionDOS =
                        grayPolicyConditionRepository.findByPolicy(grayPolicyDO.getId());
                    GrayRuleDO grayRuleDO = grayRuleRepository.findByPolicy(grayPolicyDO.getId());
                    if (grayPolicyConditionDOS == null || grayPolicyConditionDOS.size() == 0 || grayRuleDO == null) {
                        LOGGER.info("grayRule is empty return");
                        return;
                    }
                    Map<String, Object> grayRuleMap = Maps.newHashMap();
                    grayRuleMap.put("groovyScript", grayRuleDO.getRuleContent());
                    grayPolicyConditionDOS.stream()
                        .filter(grayPolicyConditionDO -> GrayParamKind.HTTP_HEADER.getCode()
                            .equalsIgnoreCase(grayPolicyConditionDO.getParamKind())
                            && YesNoKind.YES.getCode().equalsIgnoreCase(grayPolicyConditionDO.getTransmit()))
                        .forEach(grayPolicyConditionDO -> {
                            if (grayRuleMap.get("headers") == null) {
                                grayRuleMap.put("headers", new ArrayList<String>());
                            }
                            // 透传header
                            ((ArrayList<String>)grayRuleMap.get("headers")).add(grayPolicyConditionDO.getParamKey());
                        });
                    if (grayPlanMap.get(grayPolicyDO.getConsumerService()) == null) {
                        grayPlanMap.put(grayPolicyDO.getConsumerService(), Maps.newHashMap());
                    }
                    if (grayPlanMap.get(grayPolicyDO.getConsumerService())
                        .get(grayPolicyDO.getConsumerService() + "," + grayPolicyDO.getProviderService()) == null) {
                        grayPlanMap.get(grayPolicyDO.getConsumerService()).put(
                            grayPolicyDO.getConsumerService() + "," + grayPolicyDO.getProviderService(),
                            Lists.newArrayList());
                    }
                    ((ArrayList<Object>)grayPlanMap.get(grayPolicyDO.getConsumerService())
                        .get(grayPolicyDO.getConsumerService() + "," + grayPolicyDO.getProviderService()))
                            .add(grayRuleMap);
                });
            });
        return grayPlanMap;
    }
}
