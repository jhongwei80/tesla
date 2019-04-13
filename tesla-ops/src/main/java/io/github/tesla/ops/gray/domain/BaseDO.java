package io.github.tesla.ops.gray.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * base实体
 * 
 * @author xujin
 */
public class BaseDO implements Serializable {

    private static final long serialVersionUID = 3574467599831572590L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("gmt_create")
    private Timestamp gmtCreate;

    @TableField("gmt_modified")
    private Timestamp gmtModified;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }
}
