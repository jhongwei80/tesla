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
package io.github.tesla.ops.gwservice.vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author liushiming
 * @version ApiVo.java, v 0.0.1 2018年4月17日 下午2:17:58 liushiming
 */
public class GatewayServiceVo implements Serializable {

    private static final long serialVersionUID = 8303012923548625829L;

    private Long id;

    private String serviceId;

    private String serviceName;

    private String serviceDesc;

    private String serviceEnabled;

    private String approvalStatus;

    private String serviceOwner;

    private String servicePrefix;

    private String servicePath;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    public String getApprovalStatus() {
        return approvalStatus;
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

    public String getServicePath() {
        return servicePath;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
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

    public void setServicePath(String servicePath) {
        this.servicePath = servicePath;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }
}
