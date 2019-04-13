/*
 * Copyright 2014-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.github.tesla.ops.gwservice.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import io.github.tesla.common.dao.*;
import io.github.tesla.common.domain.*;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.common.service.GatewayApiTextService;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.ops.common.MultiGatewayUrlSwitcher;
import io.github.tesla.ops.common.OperatingTypeEnum;
import io.github.tesla.ops.gwservice.vo.GatewayServiceVo;
import io.github.tesla.ops.gwservice.vo.ServiceVO;
import io.github.tesla.ops.system.domain.PageDO;
import io.github.tesla.ops.system.service.DataBackupService;
import io.github.tesla.ops.utils.Query;
import io.github.tesla.ops.utils.StringUtils;

/**
 * @author liushiming
 * @version routeServiceImpl.java, v 0.0.1 2018年1月8日 上午11:38:49 liushiming
 */
@Service
public class GatewayService {

    @Autowired
    private GatewayServiceMapper serviceMapper;
    @Autowired
    private GatewayEndpointMapper endpointMapper;
    @Autowired
    private GatewayEndpointPluginMapper endpointPluginMapper;
    @Autowired
    private GatewayServiceRouterMapper routerMapper;
    @Autowired
    private GatewayServicePluginMapper servicePluginMapper;
    @Autowired
    private GatewayFileMapper fileMapper;
    @Autowired
    private DataBackupService dataBackupService;
    @Autowired
    private GatewayApiTextService apiTextService;

    public List<Map<String, Object>> queryServiceSelect() {
        List<Map<String, Object>> select = Lists.newArrayList();
        List<GatewayServiceDO> gatewayServiceDOS = serviceMapper.selectByMap(null);
        if (CollectionUtils.isEmpty(gatewayServiceDOS)) {
            return select;
        }
        for (GatewayServiceDO serviceDO : gatewayServiceDOS) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", serviceDO.getId());
            map.put("serviceId", serviceDO.getServiceId());
            map.put("name", serviceDO.getServiceName());
            map.put("path", AntMatchUtil.path(serviceDO.getServicePrefix()));
            select.add(map);
        }
        return select;
    }

    public PageDO<GatewayServiceVo> queryList(Query query) {
        int pageNo = Integer.parseInt(String.valueOf(query.remove("page")));
        int pageSize = Integer.parseInt(String.valueOf(query.remove("limit")));
        query.remove("offset");
        if (StringUtils.isBlank(MapUtils.getString(query, "id"))) {
            query.remove("id");
        }
        IPage iPage = serviceMapper.selectPage(new Page(pageNo, pageSize),
            Wrappers.<GatewayServiceDO>query().allEq(query).orderByDesc("id"));
        List<GatewayServiceDO> serviceDOList = iPage.getRecords();

        List<GatewayServiceVo> serviceVoList = Lists.newArrayList();
        for (GatewayServiceDO serviceDO : serviceDOList) {
            GatewayServiceVo serviceVo = new GatewayServiceVo();
            BeanUtils.copyProperties(serviceDO, serviceVo);
            serviceVo.setServicePath(
                MultiGatewayUrlSwitcher.getGatewayUrl() + AntMatchUtil.path(serviceDO.getServicePrefix()));
            serviceVoList.add(serviceVo);
        }
        PageDO<GatewayServiceVo> pageInfo = new PageDO<>();
        pageInfo.setRows(serviceVoList);
        pageInfo.setTotal((int)iPage.getTotal());
        query.setLimit(pageSize);
        query.setOffset(pageNo);
        pageInfo.setParams(query);
        return pageInfo;
    }

    public ServiceDTO get(Long id) {
        ServiceDTO serviceDTO = apiTextService.loadGatewayServiceById(id);

        if (Objects.isNull(serviceDTO)) {
            return null;
        }

        return serviceDTO;
    }

    @Transactional(rollbackFor = Throwable.class)
    public void saveService(ServiceVO serviceVO) {
        GatewayServiceDO serviceDO = serviceVO.getServiceDo();
        clearOldService(serviceDO, OperatingTypeEnum.UPDATE);
        serviceMapper.insert(serviceDO);
        GatewayServiceRouterDO routerDO = serviceVO.getServiceRouterDo();
        routerMapper.insert(routerDO);
        List<GatewayServicePluginDO> pluginDOS = serviceVO.getServicePluginDOList();
        pluginDOS.forEach(pluginDO -> servicePluginMapper.insert(pluginDO));
        List<GatewayEndpointDO> endpointDOS = serviceVO.getEndpointDOList();
        endpointDOS.forEach(endpoint -> {
            endpointMapper.insert(endpoint);
        });
        List<GatewayEndpointPluginDO> endpointPluginDOS = serviceVO.getEndpointPluginDOList();
        endpointPluginDOS.forEach(pluginDO -> endpointPluginMapper.insert(pluginDO));
        if (!MapUtils.isEmpty(PluginDefinition.uploadFileMap.get())) {
            PluginDefinition.uploadFileMap.get().keySet().forEach(fileKey -> {
                if (fileKey.contains(PluginDefinition.fileTab)) {
                    return;
                }
                GatewayFileDO fileDO = new GatewayFileDO();
                fileDO.setFileId(fileKey);
                fileDO.setFileName(fileKey);
                fileDO.setFileBlob(PluginDefinition.uploadFileMap.get().get(fileKey));
                fileMapper.insert(fileDO);
            });
        }
    }

    private void clearOldService(GatewayServiceDO serviceDO, OperatingTypeEnum operatingType) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("service_id", serviceDO.getServiceId());
        List<GatewayServiceDO> serviceDOList = serviceMapper.selectByMap(paramMap);
        if (serviceDOList != null && serviceDOList.size() == 1) {
            serviceDO.setGmtCreate(serviceDOList.get(0).getGmtCreate());
            serviceDO.setServiceOwner(serviceDOList.get(0).getServiceOwner());
            serviceDO.setApprovalStatus(serviceDOList.get(0).getApprovalStatus());
            dataBackupService.backup(serviceDOList.get(0), operatingType);
            serviceMapper.deleteById(serviceDOList.get(0).getId());
            List<GatewayEndpointDO> endpointDOS = endpointMapper.selectByMap(paramMap);
            endpointDOS.forEach(e -> {
                Map<String, Object> endpointParamMap = Maps.newHashMap();
                endpointParamMap.put("endpoint_id", e.getEndpointId());
                List<GatewayEndpointPluginDO> endpointPluginDOS = endpointPluginMapper.selectByMap(endpointParamMap);
                endpointPluginDOS.forEach(plugin -> {
                    dataBackupService.backup(plugin, operatingType);
                    endpointPluginMapper.deleteById(plugin.getId());
                });
                dataBackupService.backup(e, operatingType);
                endpointMapper.deleteById(e.getId());
            });

            List<GatewayServiceRouterDO> routerDOS = routerMapper.selectByMap(paramMap);
            routerDOS.forEach(e -> {
                dataBackupService.backup(e, operatingType);
                routerMapper.deleteById(e.getId());
            });
            List<GatewayServicePluginDO> servicePluginDOS = servicePluginMapper.selectByMap(paramMap);
            servicePluginDOS.forEach(e -> {
                dataBackupService.backup(e, operatingType);
                servicePluginMapper.deleteById(e.getId());
            });
        }
    }

    public void deleteService(Long id) {
        GatewayServiceDO serviceDO = serviceMapper.selectById(id);
        clearOldService(serviceDO, OperatingTypeEnum.DELETE);
    }

    public ServiceVO exportService(Long id) {
        ServiceDTO dto = apiTextService.loadGatewayServiceById(id);
        ServiceVO serviceVO = new ServiceVO();
        BeanUtils.copyProperties(dto, serviceVO);
        return serviceVO.getExportVo();
    }

    public GatewayServiceDO findServiceByServiceId(String serviceId) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("service_id", serviceId);
        return getGatewayServiceDO(paramMap);
    }

    private GatewayServiceDO getGatewayServiceDO(Map<String, Object> paramMap) {
        List<GatewayServiceDO> serviceDOList = serviceMapper.selectByMap(paramMap);
        if (serviceDOList != null && serviceDOList.size() == 1) {
            return serviceDOList.get(0);
        }
        return null;
    }

    public boolean review(Long id) {
        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("id", id);
        GatewayServiceDO gatewayServiceDO = getGatewayServiceDO(paramMap);
        if (gatewayServiceDO == null) {
            return false;
        }
        gatewayServiceDO.setApprovalStatus(YesOrNoEnum.YES.getCode());
        return serviceMapper.updateById(gatewayServiceDO) == 1;
    }
}
