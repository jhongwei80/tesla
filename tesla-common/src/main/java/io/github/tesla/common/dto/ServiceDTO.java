package io.github.tesla.common.dto;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author: zhangzhiping
 * @date: 2018/11/26 17:40
 * @description:
 */
public class ServiceDTO {
    private Long id;

    private String serviceId;

    private String serviceName;

    private String serviceDesc;

    private String serviceEnabled;

    private String approvalStatus;

    private String serviceOwner;

    private String servicePrefix;

    private List<ServicePluginDTO> pluginDTOList;

    private ServiceRouterDTO routerDTO;

    private List<EndpointDTO> endpointDTOList;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public List<EndpointDTO> getEndpointDTOList() {
        return endpointDTOList;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public Long getId() {
        return id;
    }

    public List<ServicePluginDTO> getPluginDTOList() {
        return pluginDTOList;
    }

    public ServiceRouterDTO getRouterDTO() {
        return routerDTO;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public String getServiceEnabled() {
        return serviceEnabled;
    }

    public String getServiceId() {
        return serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getServiceOwner() {
        return serviceOwner;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public void setEndpointDTOList(List<EndpointDTO> endpointDTOList) {
        this.endpointDTOList = endpointDTOList;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPluginDTOList(List<ServicePluginDTO> pluginDTOList) {
        this.pluginDTOList = pluginDTOList;
    }

    public void setRouterDTO(ServiceRouterDTO routerDTO) {
        this.routerDTO = routerDTO;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public void setServiceEnabled(String serviceEnabled) {
        this.serviceEnabled = serviceEnabled;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setServiceOwner(String serviceOwner) {
        this.serviceOwner = serviceOwner;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }
}
