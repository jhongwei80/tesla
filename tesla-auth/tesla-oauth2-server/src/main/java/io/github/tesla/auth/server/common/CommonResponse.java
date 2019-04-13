package io.github.tesla.auth.server.common;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

public class CommonResponse extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    private static int SUCCESS = 1;
    private static int ERROR = 0;

    public CommonResponse() {
        put("code", SUCCESS);
        put("msg", "操作成功");
    }

    public static CommonResponse error() {
        return error(ERROR, "操作失败");
    }

    public static CommonResponse error(String msg) {
        return error(500, msg);
    }

    public static CommonResponse error(int code, String msg) {
        CommonResponse r = new CommonResponse();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static CommonResponse ok(String msg) {
        CommonResponse r = new CommonResponse();
        r.put("msg", msg);
        return r;
    }

    public static CommonResponse ok(Map<String, Object> map) {
        CommonResponse r = new CommonResponse();
        r.putAll(map);
        return r;
    }

    public static CommonResponse ok(String key, Object value) {
        CommonResponse r = new CommonResponse();
        r.put(key, value);
        return r;
    }

    public static CommonResponse ok() {
        return new CommonResponse();
    }

    @Override
    public CommonResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public boolean isSuccess() {
        return MapUtils.getInteger(this, "code") == SUCCESS;
    }
}
