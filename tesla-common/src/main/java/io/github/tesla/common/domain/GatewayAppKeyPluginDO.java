package io.github.tesla.common.domain;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
@TableName("gateway_app_plugin")
public class GatewayAppKeyPluginDO extends GatewayCommonPluginDO {

    private static final long serialVersionUID = 4143908729970067879L;

    protected String appKeyId;

    public String getAppKeyId() {
        return appKeyId;
    }

    public void setAppKeyId(String appKeyId) {
        this.appKeyId = appKeyId;
    }
}
