package io.github.tesla.ops.common;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 基础返回
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/23
 * @version: V1.0.0
 * @since JDK 11 ${TAGS}
 */
public class BaseResult {

    public static BaseResult fail() {
        BaseResult result = new BaseResult();
        result.setSuccess(false);
        return result;
    }

    public static BaseResult fail(String returnCode) {
        BaseResult result = new BaseResult();
        result.setSuccess(false);
        result.setReturnCode(returnCode);
        return result;
    }

    public static BaseResult fail(String returnCode, String returnMessage) {
        BaseResult result = new BaseResult();
        result.setSuccess(false);
        result.setReturnCode(returnCode);
        result.setReturnMessage(returnMessage);
        return result;
    }

    public static BaseResult failM(String returnMessage) {
        BaseResult result = new BaseResult();
        result.setSuccess(false);
        result.setReturnMessage(returnMessage);
        return result;
    }

    public static BaseResult success() {
        BaseResult result = new BaseResult();
        result.setSuccess(true);
        return result;
    }

    public static BaseResult success(String returnMessage) {
        BaseResult result = new BaseResult();
        result.setSuccess(true);
        result.setReturnMessage(returnMessage);
        return result;
    }

    public static BaseResult success(String returnCode, String returnMessage) {
        BaseResult result = new BaseResult();
        result.setSuccess(true);
        result.setReturnCode(returnCode);
        result.setReturnMessage(returnMessage);
        return result;
    }

    private boolean success;

    private String returnCode;

    private String returnMessage;

    private Map<String, Object> data = Maps.newHashMap();

    public Map<String, Object> getData() {
        return data;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void put(String key, Object value) {
        this.data.put(key, value);
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
