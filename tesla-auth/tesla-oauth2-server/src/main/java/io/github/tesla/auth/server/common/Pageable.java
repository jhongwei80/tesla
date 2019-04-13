package io.github.tesla.auth.server.common;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class Pageable implements Serializable {
    private static final long serialVersionUID = 1L;
    // 总记录数
    private int total;
    // 列表数据
    private List<?> rows;

    /**
     * @desc:分页
     * @method: Pageable
     * @param: [list,
     *             total]列表数据
     * @return:
     * @auther: zhipingzhang
     * @date: 2019/1/2 11:03
     */
    public Pageable(List<?> list, int total) {
        this.rows = list;
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<?> getRows() {
        return rows;
    }

    public void setRows(List<?> rows) {
        this.rows = rows;
    }

}
