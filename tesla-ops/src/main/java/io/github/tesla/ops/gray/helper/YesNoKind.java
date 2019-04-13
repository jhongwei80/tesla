package io.github.tesla.ops.gray.helper;

/**
 * 是否
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 15:17
 * @version: V1.0.0
 * @since JDK 11
 */
public enum YesNoKind {

    YES("Y", 1, "是") {
        @Override
        public YesNoKind revert() {
            return YesNoKind.NO;
        }
    },
    NO("N", 0, "否") {
        @Override
        public YesNoKind revert() {
            return YesNoKind.YES;
        }
    },;

    public static YesNoKind get(String code) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(code)) {
            return null;
        }
        for (YesNoKind target : YesNoKind.values()) {
            if (code.equalsIgnoreCase(target.getCode())) {
                return target;
            }
        }
        return null;
    }

    private String code;

    private Integer dbCode;

    private String msg;

    YesNoKind(String code, Integer dbCode, String msg) {
        this.code = code;
        this.dbCode = dbCode;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public Integer getDbCode() {
        return dbCode;
    }

    public String getMsg() {
        return msg;
    }

    public abstract YesNoKind revert();
}
