package io.github.tesla.common.domain;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
@TableName("gateway_service_plugin")
public class GatewayServicePluginDO extends GatewayCommonPluginDO {

    private static final long serialVersionUID = -1460627977393848681L;
    protected String serviceId;

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
