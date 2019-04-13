package io.github.tesla.auth.common.support;

public class APIResult {
    private Result result;
    private Object content;

    APIResult() {
        result = new Result();
        content = new Object();
    }

    public APIResult(ErrorCode errorCode) {
        this(errorCode, errorCode.getDesc(), null);
    }

    APIResult(ErrorCode errorCode, String message, Object content) {
        this.result = new Result(errorCode.getCode(), message);
        this.content = content;
    }

    public APIResult(Integer errorCode, String errorMsg, Object content) {
        this.result = new Result(errorCode, errorMsg);
        this.content = content;
    }

    public APIResult(ErrorCode errorCode, Object content) {
        this(errorCode, errorCode.getDesc(), content);
    }

    public static APIResult success() {
        return success(null);
    }

    public static APIResult success(Object data) {
        return new APIResult(ErrorCode.OK, data);
    }

    public static APIResult success(String message, Object data) {
        return new APIResult(ErrorCode.OK, message, data);
    }

    public static APIResult error(ErrorCode errorCode, String message) {
        return new APIResult(errorCode, message, null);
    }

    public static APIResult error(ErrorCode errorCode) {
        return new APIResult(errorCode, errorCode.getDesc(), null);
    }

    public static APIResult error(ErrorCode errorCode, String message, Object data) {
        return new APIResult(errorCode, message, data);
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}