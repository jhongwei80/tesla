package io.github.tesla.ops.common;

public class Constant {

    /**
     * 部门根节点ID
     */
    public static final Long DEPT_ROOT_ID = 0L;

    /**
     * 过滤器类型:入口
     */
    public static final String FILTER_TYPE_IN = "IN";

    /**
     * 过滤器类型:出口
     */
    public static final String FILTER_TYPE_OUT = "OUT";

    /**
     * 用户状态:正常
     */
    public static final Integer USER_STATUS_NORMAL = 1;
    /**
     * 用户状态:禁用
     */
    public static final Integer USER_STATUS_DISABLE = 0;

    /**
     * 灰度规则模板类型:freemarker
     */
    public static final String GRAY_RULE_TEMPLATE_FTL = "FTL";

    public static final String GRAY_TEMPLATE_PARAM_KEY_CONDITIONS = "conditions";
    public static final String GRAY_TEMPLATE_PARAM_KEY_NODES = "nodes";

    public static final String GATEWAY_APP_NAME = "TESLA-GATEWAY";

    public static final String PLUGIN_TYPE_ENDPOINT = "endpoint";

}
