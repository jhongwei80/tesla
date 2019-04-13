package io.github.tesla.common.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.tesla.common.dao.*;
import io.github.tesla.common.domain.*;
import io.github.tesla.common.dto.*;

/**
 * 从数据库加载文本定义插件
 * 
 * @author: zhangzhiping
 * @date: 2018/11/26 17:44
 * @description: 提供返回深度构建的DTO的方法
 */
@Service
public class GatewayApiTextService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GatewayApiTextService.class);

    @Autowired
    private GatewayServiceMapper gatewayServiceMapper;
    @Autowired
    private GatewayEndpointMapper gatewayEndpointMapper;
    @Autowired
    private GatewayServicePluginMapper gatewayServicePluginMapper;
    @Autowired
    private GatewayServiceRouterMapper gatewayServiceRouterMapper;
    @Autowired
    private GatewayEndpointPluginMapper gatewayEndpointPluginMapper;
    @Autowired
    private GatewayAppKeyMapper gatewayAppKeyMapper;
    @Autowired
    private GatewayAppKeyPluginMapper gatewayAppKeyPluginMapper;

    public ServiceDTO loadGatewayServiceByServiceId(String serviceId) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("service_id", serviceId);
        List<ServiceDTO> serviceDTOS = loadGatewayServicesByParam(paramMap);
        if (serviceDTOS != null && serviceDTOS.size() == 1) {
            return serviceDTOS.get(0);
        }
        return null;
    }

    public ServiceDTO loadGatewayServiceById(Long id) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("id", id);
        List<ServiceDTO> serviceDTOS = loadGatewayServicesByParam(paramMap);
        if (serviceDTOS != null && serviceDTOS.size() == 1) {
            return serviceDTOS.get(0);
        }
        return null;
    }

    private List<ServiceDTO> loadGatewayServicesByParam(Map<String, Object> paramMap) {
        List<GatewayServiceDO> serviceDOS = gatewayServiceMapper.selectByMap(paramMap);
        if (serviceDOS == null || serviceDOS.size() == 0) {
            return Collections.emptyList();
        }
        List<ServiceDTO> serviceDTOS = Lists.newArrayList();
        for (GatewayServiceDO service : serviceDOS) {
            try {
                Map<String, Object> innerParamMap = Maps.newHashMap();
                // 构造service基础信息
                ServiceDTO serviceDTO = new ServiceDTO();
                BeanUtils.copyProperties(service, serviceDTO);
                // 构造router信息
                innerParamMap.put("service_id", service.getServiceId());
                GatewayServiceRouterDO routerDO = gatewayServiceRouterMapper.selectByMap(innerParamMap).get(0);
                ServiceRouterDTO serviceRouterDTO = new ServiceRouterDTO();
                BeanUtils.copyProperties(routerDO, serviceRouterDTO);
                serviceDTO.setRouterDTO(serviceRouterDTO);
                // 构造serviceFilterList信息
                List<GatewayServicePluginDO> servicePluginDOS = gatewayServicePluginMapper.selectByMap(innerParamMap);
                List<ServicePluginDTO> servicePluginDTOS = Lists.newArrayList();
                for (GatewayServicePluginDO pluginDO : servicePluginDOS) {
                    ServicePluginDTO servicePluginDTO = new ServicePluginDTO();
                    BeanUtils.copyProperties(pluginDO, servicePluginDTO);
                    servicePluginDTOS.add(servicePluginDTO);
                }
                serviceDTO.setPluginDTOList(servicePluginDTOS);
                // 构造endpointList
                List<GatewayEndpointDO> endpointDOS = gatewayEndpointMapper.selectByMap(innerParamMap);
                List<EndpointDTO> endpointDTOS = Lists.newArrayList();
                for (GatewayEndpointDO endpointDO : endpointDOS) {
                    EndpointDTO endpointDTO = new EndpointDTO();
                    BeanUtils.copyProperties(endpointDO, endpointDTO);
                    innerParamMap.clear();
                    innerParamMap.put("endpoint_id", endpointDO.getEndpointId());
                    List<GatewayEndpointPluginDO> endpointPluginDOS =
                        gatewayEndpointPluginMapper.selectByMap(innerParamMap);
                    List<EndpointPluginDTO> pluginDTOS = Lists.newArrayList();
                    for (GatewayEndpointPluginDO pluginDO : endpointPluginDOS) {
                        EndpointPluginDTO pluginDTO = new EndpointPluginDTO();
                        BeanUtils.copyProperties(pluginDO, pluginDTO);
                        pluginDTOS.add(pluginDTO);
                    }
                    endpointDTO.setPluginDTOList(pluginDTOS);
                    endpointDTOS.add(endpointDTO);
                }
                serviceDTO.setEndpointDTOList(endpointDTOS);
                serviceDTOS.add(serviceDTO);
            } catch (Exception e) {
                LOGGER.error(String.format("construction %s DTO failure，error:", service.getServiceName()), e);
                throw e;
            }
        }
        return serviceDTOS;
    }

    public List<ServiceDTO> loadEnabledService() {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("service_enabled", "Y");
        paramMap.put("approval_status", "Y");
        return this.loadGatewayServicesByParam(paramMap);
    }

    public List<AppKeyDTO> loadEnabledAppKey() {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("app_key_enabled", "Y");
        List<GatewayAppKeyDO> appKeyDOS = gatewayAppKeyMapper.selectByMap(paramMap);
        if (appKeyDOS == null || appKeyDOS.size() == 0) {
            return Collections.emptyList();
        }
        List<AppKeyDTO> appKeyDTOS = Lists.newArrayList();
        for (GatewayAppKeyDO appKeyDO : appKeyDOS) {
            try {
                Map<String, Object> innerParamMap = Maps.newHashMap();
                // 构造appKey基础信息
                AppKeyDTO appKeyDTO = new AppKeyDTO();
                BeanUtils.copyProperties(appKeyDO, appKeyDTO);
                // 构造appKeyPlugin信息
                innerParamMap.put("app_key_id", appKeyDO.getAppKeyId());
                // 构造serviceFilterList信息
                List<GatewayAppKeyPluginDO> appKeyPluginDOS = gatewayAppKeyPluginMapper.selectByMap(innerParamMap);
                List<AppKeyPluginDTO> appKeyPluginDTOS = Lists.newArrayList();
                for (GatewayAppKeyPluginDO pluginDO : appKeyPluginDOS) {
                    AppKeyPluginDTO appKeyPluginDTO = new AppKeyPluginDTO();
                    BeanUtils.copyProperties(pluginDO, appKeyPluginDTO);
                    appKeyPluginDTOS.add(appKeyPluginDTO);
                }
                appKeyDTO.setAppKeyPluginDTOS(appKeyPluginDTOS);
                appKeyDTOS.add(appKeyDTO);
            } catch (Exception e) {
                LOGGER.error(String.format("construction AppKey %s DTO failure，error:", appKeyDO.getAppName()), e);
                throw e;
            }
        }
        return appKeyDTOS;
    }

}
