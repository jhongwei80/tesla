package io.github.tesla.common.dto;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
public class ServicePluginDTO extends CommonPluginDTO {

    private String serviceId;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
