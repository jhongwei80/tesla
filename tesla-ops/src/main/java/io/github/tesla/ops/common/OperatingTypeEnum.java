package io.github.tesla.ops.common;

public enum OperatingTypeEnum {
    DELETE("DELETE", "删除操作"), UPDATE("UPDATE", "删除操作");

    private String code;
    private String name;

    OperatingTypeEnum(String code, String name) {
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
