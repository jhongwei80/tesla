package io.github.tesla.ops.policy.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;

import io.github.tesla.common.dao.GatewayPolicyMapper;
import io.github.tesla.common.dao.GatewayServiceMapper;
import io.github.tesla.common.domain.GatewayPolicyDO;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.filter.utils.SnowflakeIdWorker;
import io.github.tesla.ops.policy.vo.GwPolicyParamVo;
import io.github.tesla.ops.policy.vo.GwPolicyVo;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.JSONUtils;
import io.github.tesla.ops.utils.Query;

/**
 * @ClassName GwPolicyService
 * @Description Service implementation for gateway policy
 * @Author zhouchao
 * @Date 2018/12/6 18:08
 * @Version 1.0
 **/
@Service
public class GwPolicyService {
    @Autowired
    private GatewayPolicyMapper policyMapper;
    @Autowired
    private GatewayServiceMapper serviceMapper;

    public PageDO<GwPolicyVo> queryList(Query query) {
        int pageNo = Integer.parseInt(String.valueOf(query.remove("page")));
        int pageSize = Integer.parseInt(String.valueOf(query.remove("limit")));
        query.remove("offset");
        IPage iPage =
            policyMapper.selectPage(new Page(pageNo, pageSize), Wrappers.<GatewayPolicyDO>query().allEq(query));
        List<GatewayPolicyDO> policyDOList = iPage.getRecords();
        List<GwPolicyVo> policyVoList = Lists.newArrayList();
        for (GatewayPolicyDO policyDO : policyDOList) {
            GwPolicyVo policyVo = new GwPolicyVo();
            BeanUtils.copyProperties(policyDO, policyVo);
            policyVoList.add(policyVo);
        }
        PageDO<GwPolicyVo> pageInfo = new PageDO<>();
        pageInfo.setRows(policyVoList);
        pageInfo.setTotal((int)iPage.getTotal());
        query.setLimit(pageSize);
        query.setOffset(pageNo);
        pageInfo.setParams(query);
        return pageInfo;
    }

    public GwPolicyVo getGwPolicyById2Edit(Long id) {
        GatewayPolicyDO gatewayPolicyDO = policyMapper.selectById(id);
        if (Objects.isNull(gatewayPolicyDO)) {
            return null;
        }
        GwPolicyVo policyVo = new GwPolicyVo();
        BeanUtils.copyProperties(gatewayPolicyDO, policyVo);
        GwPolicyParamVo gwPolicyParamVo = JsonUtils.fromJson(policyVo.getPolicyParam(), GwPolicyParamVo.class);
        policyVo.setPolicyParamVo(gwPolicyParamVo);
        return policyVo;
    }

    public void saveGwPolicyVo(GwPolicyVo gwPolicyVo) {
        GatewayPolicyDO gatewayPolicyDO = new GatewayPolicyDO();
        BeanUtils.copyProperties(gwPolicyVo, gatewayPolicyDO);
        buildPolicyDo(gwPolicyVo);
        gatewayPolicyDO.setPolicyId(SnowflakeIdWorker.nextId("Policy_"));
        gatewayPolicyDO.setPolicyParam(JsonUtils.serializeToJson(gwPolicyVo.getPolicyParamVo()));
        gatewayPolicyDO.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        policyMapper.insert(gatewayPolicyDO);
    }

    public void updateGwPolicyVo(GwPolicyVo gwPolicyVo) {
        GatewayPolicyDO gatewayPolicyDO = policyMapper.selectById(gwPolicyVo.getId());
        BeanUtils.copyProperties(gwPolicyVo, gatewayPolicyDO);
        buildPolicyDo(gwPolicyVo);
        gatewayPolicyDO.setPolicyParam(JSONUtils.beanToJson(gwPolicyVo.getPolicyParamVo()));
        gatewayPolicyDO.setGmtModified(new Timestamp(System.currentTimeMillis()));
        policyMapper.updateById(gatewayPolicyDO);
    }

    private void buildPolicyDo(GwPolicyVo gwPolicyVo) {
        if (Objects.equals(YesOrNoEnum.NO.getCode(), gwPolicyVo.getPolicyParamVo().getRateLimit().getEnabled())) {
            gwPolicyVo.getPolicyParamVo().setRateLimit(null);
        }
        if (Objects.isNull(gwPolicyVo.getPolicyParamVo().getQuota().getMaxRequest())
            || gwPolicyVo.getPolicyParamVo().getQuota().getMaxRequest() < 0) {
            gwPolicyVo.getPolicyParamVo().setQuota(null);
        }
    }

    public void copyById(Long id) {
        GatewayPolicyDO gatewayPolicyDO = policyMapper.selectById(id);
        gatewayPolicyDO.setId(null);
        gatewayPolicyDO.setPolicyId(SnowflakeIdWorker.nextId("Policy_"));
        gatewayPolicyDO.setGmtCreate(new Timestamp(System.currentTimeMillis()));
        gatewayPolicyDO.setPolicyName(gatewayPolicyDO.getPolicyName() + "_Copy");
        policyMapper.insert(gatewayPolicyDO);
    }

    public void removeGwPolicyVo(Long id) {
        policyMapper.deleteById(id);
    }

    public List<Map<String, String>> getAppControl() {
        return serviceMapper.getServiceInfo();
    }
}
