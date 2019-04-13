package io.github.tesla.ops.waf.service;

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

import io.github.tesla.common.dao.GatewayServiceMapper;
import io.github.tesla.common.dao.GatewayWafMapper;
import io.github.tesla.common.domain.GatewayWafDO;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.utils.Query;
import io.github.tesla.ops.waf.vo.GwWafVo;

/**
 * @ClassName GwWafService
 * @Description Service implementation for gateway waf
 * @Author zhouchao
 * @Date 2018/12/13 15:31
 * @Version 1.0
 **/
@Service
public class GwWafService {
    @Autowired
    private GatewayWafMapper gatewayWafMapper;
    @Autowired
    private GatewayServiceMapper serviceMapper;

    public PageDO<GwWafVo> queryList(Query query) {
        int pageNo = Integer.parseInt(String.valueOf(query.remove("page")));
        int pageSize = Integer.parseInt(String.valueOf(query.remove("limit")));
        query.remove("offset");
        IPage iPage =
            gatewayWafMapper.selectPage(new Page(pageNo, pageSize), Wrappers.<GatewayWafDO>query().allEq(query));
        List<GatewayWafDO> policyDOList = iPage.getRecords();
        List<GwWafVo> wafVoList = Lists.newArrayList();
        for (GatewayWafDO policyDO : policyDOList) {
            GwWafVo wafVo = new GwWafVo();
            BeanUtils.copyProperties(policyDO, wafVo);
            wafVoList.add(wafVo);
        }
        PageDO<GwWafVo> pageInfo = new PageDO<>();
        pageInfo.setRows(wafVoList);
        pageInfo.setTotal((int)iPage.getTotal());
        query.setLimit(pageSize);
        query.setOffset(pageNo);
        pageInfo.setParams(query);
        return pageInfo;
    }

    public GwWafVo getGwWafById2Edit(Long id) {
        GatewayWafDO gatewayWafDO = gatewayWafMapper.selectById(id);
        if (Objects.isNull(gatewayWafDO)) {
            return null;
        }
        GwWafVo gwWafVo = new GwWafVo();
        BeanUtils.copyProperties(gatewayWafDO, gwWafVo);
        return gwWafVo;
    }

    public void updateGwWaf(GwWafVo gwWafVo) {
        GatewayWafDO gatewayWafDO = gatewayWafMapper.selectById(gwWafVo.getId());
        BeanUtils.copyProperties(gwWafVo, gatewayWafDO, "id", "wafType", "gmtCreate", "gmtModified", "pluginParam");
        gatewayWafDO.setPluginParam(gwWafVo.validateParam());
        gatewayWafDO.setGmtModified(new Timestamp(System.currentTimeMillis()));
        gatewayWafMapper.updateById(gatewayWafDO);
    }

    public List<Map<String, String>> getAppControl() {
        return serviceMapper.getServiceInfo();
    }
}
