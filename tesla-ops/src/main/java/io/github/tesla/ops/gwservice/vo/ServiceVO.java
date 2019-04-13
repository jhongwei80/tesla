package io.github.tesla.ops.gwservice.vo;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import io.github.tesla.common.domain.*;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.support.enums.RouteTypeEnum;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.support.plugins.EndpointRequestPluginMetadata;
import io.github.tesla.filter.support.plugins.EndpointResponsePluginMetadata;
import io.github.tesla.filter.support.plugins.ServiceRequestPluginMetadata;
import io.github.tesla.filter.support.plugins.ServiceResponsePluginMetadata;
import io.github.tesla.filter.utils.AntMatchUtil;
import io.github.tesla.filter.utils.SnowflakeIdWorker;
import io.github.tesla.ops.utils.DateUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/26 17:40
 * @description:
 */
public class ServiceVO extends ServiceDTO {

    public static final String servicePrefix = "service_";
    public static final String endpointPrefix = "endpoint_";
    public static final String endpointPluginPrefix = "endpointPlugin_";
    public static final String serviceRouterPrefix = "serviceRouter_";
    public static final String servicePluginPrefix = "servicePlugin_";

    public List<GatewayEndpointDO> getEndpointDOList() {
        Preconditions.checkArgument(this.getEndpointDTOList().size() > 0, "endpoint不可为空");
        List<GatewayEndpointDO> endpointDOS = Lists.newArrayList();
        this.getEndpointDTOList().forEach(endpointDTO -> {
            endpointDTO.setServiceId(this.getServiceId());
            endpointDTO.setEndpointId(SnowflakeIdWorker.nextId(endpointPrefix));
            endpointDTO.setEndpointUrl(AntMatchUtil.path(endpointDTO.getEndpointUrl()));
            GatewayEndpointDO endpointDO = new GatewayEndpointDO();
            BeanUtils.copyProperties(endpointDTO, endpointDO);
            Preconditions.checkArgument(StringUtils.isNotBlank(endpointDO.getEndpointUrl()), "endpoint Url不可为空");
            Preconditions.checkArgument(AntMatchUtil.validatePattern(endpointDO.getEndpointUrl()), "endpoint Url格式不正确");
            endpointDO.setGmtCreate(DateUtils.getTimestampNow());
            endpointDO.setGmtModified(DateUtils.getTimestampNow());
            endpointDOS.add(endpointDO);
        });
        return endpointDOS;
    }

    public List<GatewayEndpointPluginDO> getEndpointPluginDOList() {
        List<GatewayEndpointPluginDO> endpointPluginDOS = Lists.newArrayList();
        this.getEndpointDTOList().forEach(endpointDTO -> {
            if (!CollectionUtils.isEmpty(endpointDTO.getPluginDTOList())) {
                endpointDTO.getPluginDTOList().forEach(pluginDTO -> {
                    pluginDTO.setEndpointId(endpointDTO.getEndpointId());
                    pluginDTO.setPluginId(SnowflakeIdWorker.nextId(endpointPluginPrefix));
                    GatewayEndpointPluginDO pluginDO = new GatewayEndpointPluginDO();
                    BeanUtils.copyProperties(pluginDTO, pluginDO);
                    pluginDO.setPluginParam(EndpointRequestPluginMetadata.validate(pluginDO.getPluginType(),
                        pluginDO.getPluginParam(), this, endpointDTO));
                    pluginDO.setPluginParam(EndpointResponsePluginMetadata.validate(pluginDO.getPluginType(),
                        pluginDO.getPluginParam(), this, endpointDTO));
                    pluginDO.setGmtCreate(DateUtils.getTimestampNow());
                    pluginDO.setGmtModified(DateUtils.getTimestampNow());
                    endpointPluginDOS.add(pluginDO);
                });
            }
        });
        return endpointPluginDOS;
    }

    public ServiceVO getExportVo() {
        this.setId(null);
        this.getEndpointDTOList().forEach(endpoint -> {
            endpoint.setId(null);
            endpoint.getPluginDTOList().forEach(endpointPluginDTO -> {
                endpointPluginDTO.setId(null);
            });
        });
        this.getRouterDTO().setId(null);
        this.getPluginDTOList().forEach(servicePluginDTO -> {
            servicePluginDTO.setId(null);
        });
        return this;
    }

    public GatewayServiceDO getServiceDo() {
        Preconditions.checkArgument(StringUtils.isNotBlank(this.getServiceName()), "服务名称不可为空");
        Preconditions.checkArgument(StringUtils.isNotBlank(this.getServicePrefix()), "调用前缀不可为空");
        this.setServicePrefix(AntMatchUtil.path(this.getServicePrefix()));
        GatewayServiceDO serviceDO = new GatewayServiceDO();
        serviceDO.setServiceDesc(this.getServiceDesc());
        serviceDO.setServiceEnabled(this.getServiceEnabled());
        serviceDO.setServiceName(this.getServiceName());
        serviceDO.setServicePrefix(this.getServicePrefix());
        if (StringUtils.isEmpty(this.getServiceId())) {
            this.setServiceId(SnowflakeIdWorker.nextId(servicePrefix));
        }
        serviceDO.setServiceId(this.getServiceId());
        serviceDO.setGmtCreate(DateUtils.getTimestampNow());
        serviceDO.setGmtModified(DateUtils.getTimestampNow());
        serviceDO.setServiceOwner(this.getServiceOwner());
        serviceDO.setApprovalStatus(YesOrNoEnum.NO.getCode());
        return serviceDO;
    }

    public List<GatewayServicePluginDO> getServicePluginDOList() {
        // Preconditions.checkArgument(this.getPluginDTOList().size() > 0, "服务名称不可为空");
        List<GatewayServicePluginDO> servicePluginDOS = Lists.newArrayList();
        if (CollectionUtils.isEmpty(this.getPluginDTOList())) {
            return servicePluginDOS;
        }
        this.getPluginDTOList().forEach(plugin -> {
            plugin.setPluginId(SnowflakeIdWorker.nextId(servicePluginPrefix));
            plugin.setServiceId(this.getServiceId());
            GatewayServicePluginDO pluginDO = new GatewayServicePluginDO();
            BeanUtils.copyProperties(plugin, pluginDO);
            pluginDO.setPluginParam(
                ServiceRequestPluginMetadata.validate(pluginDO.getPluginType(), pluginDO.getPluginParam(), this));
            pluginDO.setPluginParam(
                ServiceResponsePluginMetadata.validate(pluginDO.getPluginType(), pluginDO.getPluginParam(), this));
            pluginDO.setGmtCreate(DateUtils.getTimestampNow());
            pluginDO.setGmtModified(DateUtils.getTimestampNow());
            servicePluginDOS.add(pluginDO);
        });
        return servicePluginDOS;
    }

    public GatewayServiceRouterDO getServiceRouterDo() {
        GatewayServiceRouterDO routerDO = new GatewayServiceRouterDO();
        BeanUtils.copyProperties(this.getRouterDTO(), routerDO);
        routerDO.setServiceId(this.getServiceId());
        routerDO.setRouterParam(RouteTypeEnum.validate(routerDO.getRouterType(), routerDO.getRouterParam(), this));
        routerDO.setRouterId(SnowflakeIdWorker.nextId(serviceRouterPrefix));
        routerDO.setGmtCreate(DateUtils.getTimestampNow());
        routerDO.setGmtModified(DateUtils.getTimestampNow());
        return routerDO;
    }
}
