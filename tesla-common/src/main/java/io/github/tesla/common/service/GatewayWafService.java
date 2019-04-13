package io.github.tesla.common.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import io.github.tesla.common.dao.GatewayWafMapper;
import io.github.tesla.common.domain.GatewayWafDO;

@Service
public class GatewayWafService {

    @Autowired
    private GatewayWafMapper gatewayWafMapper;

    public List<GatewayWafDO> loadEnabledWaf() {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("waf_enabled", "Y");
        List<GatewayWafDO> wafDoList = gatewayWafMapper.selectByMap(paramMap);
        return wafDoList;
    }

}
