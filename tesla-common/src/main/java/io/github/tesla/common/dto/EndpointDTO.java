package io.github.tesla.common.dto;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 13:56
 * @description:
 */
public class EndpointDTO {

    private Long id;

    private String serviceId;

    private String endpointId;

    private String endpointMethod;

    private String endpointUrl;

    private List<EndpointPluginDTO> pluginDTOList;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    public String getEndpointId() {
        return endpointId;
    }

    public String getEndpointMethod() {
        return endpointMethod;
    }

    public String getEndpointUrl() {
        return endpointUrl;
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

    public List<EndpointPluginDTO> getPluginDTOList() {
        return pluginDTOList;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }

    public void setEndpointMethod(String endpointMethod) {
        this.endpointMethod = endpointMethod;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
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

    public void setPluginDTOList(List<EndpointPluginDTO> pluginDTOList) {
        this.pluginDTOList = pluginDTOList;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
