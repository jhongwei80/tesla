package io.github.tesla.gateway.excutor;

import java.io.Serializable;

/**
 * @author: zhangzhiping
 * @date: 2018/11/23 10:36
 * @description:
 */
public class CommonPluginExecutor implements Comparable<CommonPluginExecutor>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String filterType;

    protected String filterName;

    protected String paramJson;

    protected int order;

    @Override
    public int compareTo(CommonPluginExecutor o) {
        return this.order - o.getOrder();
    }

    public String getFilterName() {
        return filterName;
    }

    public String getFilterType() {
        return filterType;
    }

    public int getOrder() {
        return order;
    }

    public String getParamJson() {
        return paramJson;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setParamJson(String paramJson) {
        this.paramJson = paramJson;
    }
}
