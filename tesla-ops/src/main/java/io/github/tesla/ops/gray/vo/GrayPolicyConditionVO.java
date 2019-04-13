package io.github.tesla.ops.gray.vo;

import java.io.Serializable;

/**
 * 灰度策略条件
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 14:50
 * @version: V1.0.0
 * @since JDK 11
 */

public class GrayPolicyConditionVO implements Serializable {

    private Long id;

    private String paramKind;

    private String paramKey;

    private String paramValue;

    private String transmit;

    public Long getId() {
        return id;
    }

    public String getParamKey() {
        return paramKey;
    }

    public String getParamKind() {
        return paramKind;
    }

    public String getParamValue() {
        return paramValue;
    }

    public String getTransmit() {
        return transmit;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public void setParamKind(String paramKind) {
        this.paramKind = paramKind;
    }

    public void setParamValue(String paramValue) {
        this.paramValue = paramValue;
    }

    public void setTransmit(String transmit) {
        this.transmit = transmit;
    }
}
