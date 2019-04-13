package io.github.tesla.auth.common.support;

public enum ErrorCode {
    OK(200, "OK"), SESSION_TIMEOUT(302, "Session Timeout"), RULE_PARSE_ERROR(303, "Rule Parsing Error"),
    COMMON_VALIDATION_FAIL(304, "Validation fail"), PARAM_MISS(305, "Required Parameter is missing"),
    INTERNAL_ERROR(500, "Internal Error"), BAD_REQUEST(400, "Bad Request"), SC_UNAUTHORIZED(401, "Unauthorized");

    private int code;
    private String desc;

    ErrorCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isOk(int code) {
        return OK.getCode() == code;
    }
}