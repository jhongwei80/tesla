package io.github.tesla.common.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @Auther: zhipingzhang
 * @Date: 2018/10/30 10:33
 * @Description:
 */
@TableName("gateway_cache_refresh")
public class GatewayCacheRefreshDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Timestamp cacheModifyDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCacheModifyDate() {
        return cacheModifyDate;
    }

    public void setCacheModifyDate(Timestamp cacheModifyDate) {
        this.cacheModifyDate = cacheModifyDate;
    }
}
