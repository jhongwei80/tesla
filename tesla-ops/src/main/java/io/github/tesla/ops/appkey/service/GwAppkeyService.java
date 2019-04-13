package io.github.tesla.ops.appkey.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.tesla.common.dao.GatewayAppKeyMapper;
import io.github.tesla.common.dao.GatewayAppKeyPluginMapper;
import io.github.tesla.common.dao.GatewayPolicyMapper;
import io.github.tesla.common.dao.GatewayServiceMapper;
import io.github.tesla.common.domain.GatewayAppKeyDO;
import io.github.tesla.common.domain.GatewayAppKeyPluginDO;
import io.github.tesla.filter.common.definition.AccessControlDefinition;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.SnowflakeIdWorker;
import io.github.tesla.ops.appkey.enums.AppKeyPluginEnum;
import io.github.tesla.ops.appkey.vo.GwAppkeyVo;
import io.github.tesla.ops.appkey.vo.QuotaDefinitionVo;
import io.github.tesla.ops.appkey.vo.RateLimitDefinitionVo;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.DateUtils;
import io.github.tesla.ops.utils.Query;
import io.github.tesla.ops.utils.StringUtils;

/**
 * @ClassName GwAppkeyService
 * @Description Service implementation for gateway app key
 * @Author zhouchao
 * @Date 2018/12/11 13:25
 * @Version 1.0
 **/
@Service
public class GwAppkeyService {
    @Autowired
    private GatewayAppKeyMapper gatewayAppKeyMapper;
    @Autowired
    private GatewayAppKeyPluginMapper gatewayAppKeyPluginMapper;

    @Autowired
    private GatewayServiceMapper serviceMapper;

    @Autowired
    private GatewayPolicyMapper policyMapper;

    public PageDO<GwAppkeyVo> queryList(Query query) {
        int pageNo = Integer.parseInt(String.valueOf(query.remove("page")));
        int pageSize = Integer.parseInt(String.valueOf(query.remove("limit")));
        query.remove("offset");
        IPage iPage =
            gatewayAppKeyMapper.selectPage(new Page(pageNo, pageSize), Wrappers.<GatewayAppKeyDO>query().allEq(query));
        List<GatewayAppKeyDO> gwAppkeyDos = iPage.getRecords();
        List<GwAppkeyVo> appKeyVoList = Lists.newArrayList();
        for (GatewayAppKeyDO appKeyDO : gwAppkeyDos) {
            GwAppkeyVo appkeyVo = new GwAppkeyVo();
            BeanUtils.copyProperties(appKeyDO, appkeyVo);
            appKeyVoList.add(appkeyVo);
        }
        PageDO<GwAppkeyVo> pageInfo = new PageDO<>();
        pageInfo.setRows(appKeyVoList);
        pageInfo.setTotal((int)iPage.getTotal());
        query.setLimit(pageSize);
        query.setOffset(pageNo);
        pageInfo.setParams(query);
        return pageInfo;
    }

    public GwAppkeyVo getGwPolicyById2Edit(Long id) {
        GatewayAppKeyDO appKeyDO = gatewayAppKeyMapper.selectById(id);
        if (Objects.isNull(appKeyDO)) {
            return null;
        }
        GwAppkeyVo appkeyVo = new GwAppkeyVo();
        BeanUtils.copyProperties(appKeyDO, appkeyVo);
        Map<String, Object> innerParamMap = Maps.newHashMap();
        innerParamMap.put("app_key_id", appKeyDO.getAppKeyId());
        // 构造serviceFilterList信息
        List<GatewayAppKeyPluginDO> appKeyPluginDOS = gatewayAppKeyPluginMapper.selectByMap(innerParamMap);
        appKeyPluginDOS.forEach((appKeyPluginDO) -> {
            if (AppKeyPluginEnum.AccessControlRequestPlugin.getFilterType().equals(appKeyPluginDO.getPluginType())) {
                AccessControlDefinition sdf =
                    JsonUtils.json2Definition(appKeyPluginDO.getPluginParam(), AccessControlDefinition.class);
                appkeyVo.setAccessServices(Lists.newArrayList(sdf.getAccessServices().keySet()));
            } else if (AppKeyPluginEnum.QuotaRequestPlugin.getFilterType().equals(appKeyPluginDO.getPluginType())) {
                QuotaDefinitionVo quotaDefinition =
                    JsonUtils.json2Definition(appKeyPluginDO.getPluginParam(), QuotaDefinitionVo.class);
                if (quotaDefinition.getMaxRequest() < 0) {
                    quotaDefinition.setMaxRequest(-1l);
                    quotaDefinition.setInterval(null);
                    quotaDefinition.setTimeUtil(null);
                }
                appkeyVo.setQuota(JsonUtils.json2Definition(appKeyPluginDO.getPluginParam(), QuotaDefinitionVo.class));
            } else if (AppKeyPluginEnum.RateLimitRequestPlugin.getFilterType().equals(appKeyPluginDO.getPluginType())) {
                RateLimitDefinitionVo rateLimitDefinition =
                    JsonUtils.json2Definition(appKeyPluginDO.getPluginParam(), RateLimitDefinitionVo.class);
                if (Objects.equals("N", rateLimitDefinition.getEnabled())) {
                    rateLimitDefinition.setPerSeconds(null);
                    rateLimitDefinition.setRate(null);
                }
                appkeyVo.setRateLimit(rateLimitDefinition);
            }
        });
        return appkeyVo;
    }

    @Transactional
    public void saveGwAppkey(GwAppkeyVo gwAppkeyVo) {
        GatewayAppKeyDO gwAppkeyDO = new GatewayAppKeyDO();
        BeanUtils.copyProperties(gwAppkeyVo, gwAppkeyDO);
        cleanAppKey(gwAppkeyDO);
        if (StringUtils.isBlank(gwAppkeyDO.getAppKeyId())) {
            gwAppkeyDO.setAppKeyId(SnowflakeIdWorker.nextId("AppKeyId_"));
            gwAppkeyDO.setGmtCreate(DateUtils.getTimestampNow());
        }

        gatewayAppKeyMapper.insert(gwAppkeyDO);
        GatewayAppKeyPluginDO ratelimitPlugin = new GatewayAppKeyPluginDO();
        ratelimitPlugin.setAppKeyId(gwAppkeyDO.getAppKeyId());
        ratelimitPlugin.setPluginId(SnowflakeIdWorker.nextId("Plugin_"));
        ratelimitPlugin.setPluginName(AppKeyPluginEnum.RateLimitRequestPlugin.getFilterName());
        ratelimitPlugin.setPluginType(AppKeyPluginEnum.RateLimitRequestPlugin.getFilterType());
        RateLimitDefinitionVo rateLimitDefinition = gwAppkeyVo.getRateLimit();
        ratelimitPlugin.setPluginParam(JsonUtils.serializeToJson(rateLimitDefinition));
        ratelimitPlugin.setGmtCreate(DateUtils.getTimestampNow());
        if (Objects.equals(YesOrNoEnum.YES.getCode(), rateLimitDefinition.getEnabled())) {
            gatewayAppKeyPluginMapper.insert(ratelimitPlugin);
        }

        GatewayAppKeyPluginDO quotaPlugin = new GatewayAppKeyPluginDO();
        quotaPlugin.setAppKeyId(gwAppkeyDO.getAppKeyId());
        quotaPlugin.setPluginId(SnowflakeIdWorker.nextId("Plugin_"));
        quotaPlugin.setPluginName(AppKeyPluginEnum.QuotaRequestPlugin.getFilterName());
        quotaPlugin.setPluginType(AppKeyPluginEnum.QuotaRequestPlugin.getFilterType());
        QuotaDefinitionVo quotaDefinition = gwAppkeyVo.getQuota();
        quotaPlugin.setPluginParam(JsonUtils.serializeToJson(quotaDefinition));
        quotaPlugin.setGmtCreate(DateUtils.getTimestampNow());
        if (quotaDefinition.getMaxRequest() != null && quotaDefinition.getMaxRequest() > 0) {
            gatewayAppKeyPluginMapper.insert(quotaPlugin);
        }

        GatewayAppKeyPluginDO accessControlsPlugin = new GatewayAppKeyPluginDO();
        accessControlsPlugin.setAppKeyId(gwAppkeyDO.getAppKeyId());
        accessControlsPlugin.setPluginId(SnowflakeIdWorker.nextId("Plugin_"));
        accessControlsPlugin.setPluginName(AppKeyPluginEnum.AccessControlRequestPlugin.getFilterName());
        accessControlsPlugin.setPluginType(AppKeyPluginEnum.AccessControlRequestPlugin.getFilterType());
        AccessControlDefinition access = new AccessControlDefinition();
        Map<String, String> map = Maps.newHashMap();
        List<Map<String, String>> serviceMap = serviceMapper.getServicePrefixInfo();
        serviceMap.stream().forEach((sm) -> {
            map.put(sm.get("service_id"), sm.get("service_prefix"));
        });
        Map<String, String> accessServices = Maps.newHashMap();
        gwAppkeyVo.getAccessServices().forEach((as) -> {
            accessServices.put(as, map.get(as));
        });
        access.setAccessServices(accessServices);
        accessControlsPlugin.setPluginParam(JsonUtils.serializeToJson(access));
        accessControlsPlugin.setGmtCreate(DateUtils.getTimestampNow());
        if (!CollectionUtils.isEmpty(accessServices)) {
            gatewayAppKeyPluginMapper.insert(accessControlsPlugin);
        }
    }

    private void cleanAppKey(GatewayAppKeyDO newAppkeyDO) {
        if (newAppkeyDO.getId() != null) {
            GatewayAppKeyDO oldAppKeyDO = gatewayAppKeyMapper.selectById(newAppkeyDO.getId());
            newAppkeyDO.setGmtCreate(oldAppKeyDO.getGmtCreate());
            newAppkeyDO.setAppKeyId(oldAppKeyDO.getAppKeyId());
            removeGwAppkey(oldAppKeyDO.getId());
        }
    }

    @Transactional
    public void removeGwAppkey(Long id) {
        GatewayAppKeyDO gatewayAppKeyDO = gatewayAppKeyMapper.selectById(id);
        Map<String, Object> innerParamMap = Maps.newHashMap();
        innerParamMap.put("app_key_id", gatewayAppKeyDO.getAppKeyId());
        // 构造serviceFilterList信息
        List<GatewayAppKeyPluginDO> appKeyPluginDOS = gatewayAppKeyPluginMapper.selectByMap(innerParamMap);
        appKeyPluginDOS.forEach((appKeyPluginDO) -> {
            gatewayAppKeyPluginMapper.deleteById(appKeyPluginDO.getId());
        });
        gatewayAppKeyMapper.deleteById(gatewayAppKeyDO.getId());
    }

    @Transactional
    public void copyById(Long id) {
        GatewayAppKeyDO gatewayAppKeyDO = gatewayAppKeyMapper.selectById(id);
        Map<String, Object> innerParamMap = Maps.newHashMap();
        innerParamMap.put("app_key_id", gatewayAppKeyDO.getAppKeyId());

        gatewayAppKeyDO.setAppKeyId(SnowflakeIdWorker.nextId("AppKeyId_"));
        gatewayAppKeyDO.setId(null);
        gatewayAppKeyDO.setAppKey(SnowflakeIdWorker.nextId("AppKey_"));
        gatewayAppKeyDO.setAppName(gatewayAppKeyDO.getAppName() + "_Copy");
        gatewayAppKeyDO.setGmtCreate(DateUtils.getTimestampNow());
        gatewayAppKeyMapper.insert(gatewayAppKeyDO);

        // 构造serviceFilterList信息
        List<GatewayAppKeyPluginDO> appKeyPluginDOS = gatewayAppKeyPluginMapper.selectByMap(innerParamMap);
        appKeyPluginDOS.forEach((appKeyPluginDO) -> {
            appKeyPluginDO.setId(null);
            appKeyPluginDO.setAppKeyId(gatewayAppKeyDO.getAppKeyId());
            appKeyPluginDO.setPluginId(SnowflakeIdWorker.nextId("Plugin_"));
            appKeyPluginDO.setGmtCreate(DateUtils.getTimestampNow());
            gatewayAppKeyPluginMapper.insert(appKeyPluginDO);
        });
    }

    public List<Map<String, String>> getAppControl() {
        return serviceMapper.getServiceInfo();
    }

    public List<Map<String, String>> getPolicyInfo() {
        return policyMapper.getPolicyInfo();
    }

    public Map<String, String> getAppKeyMap() {
        List<GatewayAppKeyDO> gatewayAppKeyDOS = gatewayAppKeyMapper.selectByMap(null);
        Map<String, String> appKeyMap = Maps.newHashMap();
        if (!CollectionUtils.isEmpty(gatewayAppKeyDOS)) {
            gatewayAppKeyDOS.forEach(appKey -> appKeyMap.put(appKey.getAppKey(), appKey.getAppName()));
        }
        return appKeyMap;
    }
}
