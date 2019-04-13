package io.github.tesla.common.dto;

import java.sql.Timestamp;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
public class ServiceRouterDTO {

    private Long id;

    private String serviceId;

    private String routerId;

    private String routerType;

    private String routerParam;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public Long getId() {
        return id;
    }

    public String getRouterId() {
        return routerId;
    }

    public String getRouterParam() {
        return routerParam;
    }

    public String getRouterType() {
        return routerType;
    }

    public String getServiceId() {
        return serviceId;
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

    public void setRouterId(String routerId) {
        this.routerId = routerId;
    }

    public void setRouterParam(String routerParam) {
        this.routerParam = routerParam;
    }

    public void setRouterType(String routerType) {
        this.routerType = routerType;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
