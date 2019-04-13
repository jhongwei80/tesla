package io.github.tesla.auth.server.common;

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

    public static final String resourceIdHeader = "Resource-Id";

    public static final String resourceIdParam = "resource_id";
}
