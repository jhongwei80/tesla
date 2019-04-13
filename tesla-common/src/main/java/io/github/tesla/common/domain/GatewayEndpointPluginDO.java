package io.github.tesla.common.domain;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
@TableName("gateway_endpoint_plugin")
public class GatewayEndpointPluginDO extends GatewayCommonPluginDO {

    private static final long serialVersionUID = -5774515519348499623L;
    protected String endpointId;

    public String getEndpointId() {
        return endpointId;
    }

    public void setEndpointId(String endpointId) {
        this.endpointId = endpointId;
    }
}
