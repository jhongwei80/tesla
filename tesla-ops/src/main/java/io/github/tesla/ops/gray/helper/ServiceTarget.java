package io.github.tesla.ops.gray.helper;

/**
 * 服务目标
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 15:17
 * @version: V1.0.0
 * @since JDK 11
 */
public enum ServiceTarget {

    CONSUMER("CONSUMER", "消费者"), PROVIDER("PROVIDER", "提供者"),;

    public static ServiceTarget get(String code) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(code)) {
            return null;
        }
        for (ServiceTarget target : ServiceTarget.values()) {
            if (code.equalsIgnoreCase(target.getCode())) {
                return target;
            }
        }
        return null;
    }

    private String code;

    private String msg;

    ServiceTarget(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
