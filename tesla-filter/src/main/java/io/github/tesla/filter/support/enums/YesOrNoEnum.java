package io.github.tesla.filter.support.enums;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 14:41
 * @description:
 */
public enum YesOrNoEnum {
    YES("Y", "是"), NO("N", "否");

    private String code;

    private String name;

    YesOrNoEnum(String code, String name) {
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
