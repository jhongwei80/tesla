package io.github.tesla.ops.system.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PageDO<T> {

    private int offset;
    private int limit;
    private int total;
    private Map<String, Object> params;
    private String param;
    private List<T> rows;

    public PageDO() {
        super();
        this.offset = 0;
        this.limit = 10;
        this.total = 1;
        this.params = new HashMap<>();
        this.param = "";
        this.rows = new ArrayList<>();
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public String getParam() {
        return param;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public List<T> getRows() {
        return rows;
    }

    public int getTotal() {
        return total;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setParam(String param) {
        this.param = param;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "PageDO{" + "offset=" + offset + ", limit=" + limit + ", total=" + total + ", params=" + params
            + ", param='" + param + '\'' + ", rows=" + rows + '}';
    }
}
