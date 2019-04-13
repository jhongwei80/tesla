package io.github.tesla.filter.support.enums;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 14:41
 * @description:
 */
public enum HttpMethodEnum {
    ALL("ALL", "适配全部"), POST("POST", "POST"), GET("GET", "GET"), PUT("PUT", "PUT"), DELETE("DELETE", "DELETE"),
    PATCH("PATCH", "PATCH");

    public static boolean match(String endPointMethod, String method) {
        if (endPointMethod.equalsIgnoreCase(ALL.code)) {
            return true;
        } else return endPointMethod.equalsIgnoreCase(method);
    }

    private String code;

    private String name;

    HttpMethodEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
