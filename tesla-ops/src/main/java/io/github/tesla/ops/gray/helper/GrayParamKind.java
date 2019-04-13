package io.github.tesla.ops.gray.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * 灰度策略参数类型
 *
 * @author: caiyu.ren </br>
 *          Created on 2018/10/31 15:17
 * @version: V1.0.0
 * @since JDK 11
 */
public enum GrayParamKind {

    HTTP_HEADER("HTTP_HEADER", "请求头", ServiceTarget.CONSUMER), HTTP_PARAM("HTTP_PARAM", "请求参数", ServiceTarget.CONSUMER),
    HTTP_BODY("HTTP_BODY", "请求体", ServiceTarget.CONSUMER), NODE("NODE", "节点", ServiceTarget.PROVIDER),;

    public static List<GrayParamKind> get(ServiceTarget serviceTarget) {
        List<GrayParamKind> result = new ArrayList<>();
        for (GrayParamKind kind : GrayParamKind.values()) {
            if (kind.target == serviceTarget) {
                result.add(kind);
            }
        }
        return result;
    }

    public static GrayParamKind get(String code) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(code)) {
            return null;
        }
        for (GrayParamKind kind : GrayParamKind.values()) {
            if (code.equalsIgnoreCase(kind.getCode())) {
                return kind;
            }
        }
        return null;
    }

    private String code;

    private String msg;

    private ServiceTarget target;

    GrayParamKind(String code, String msg, ServiceTarget target) {
        this.code = code;
        this.msg = msg;
        this.target = target;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ServiceTarget getTarget() {
        return target;
    }
}
