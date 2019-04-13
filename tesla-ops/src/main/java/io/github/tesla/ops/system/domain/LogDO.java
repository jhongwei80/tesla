package io.github.tesla.ops.system.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class LogDO {
    private Long id;

    private Long userId;

    private String username;

    private String operation;

    private Integer time;

    private String method;

    private String params;

    private String ip;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public Long getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public String getMethod() {
        return method;
    }

    public String getOperation() {
        return operation;
    }

    public String getParams() {
        return params;
    }

    public Integer getTime() {
        return time;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    public void setMethod(String method) {
        this.method = method == null ? null : method.trim();
    }

    public void setOperation(String operation) {
        this.operation = operation == null ? null : operation.trim();
    }

    public void setParams(String params) {
        this.params = params == null ? null : params.trim();
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    @Override
    public String toString() {
        return "LogDO{" + "id=" + id + ", userId=" + userId + ", username='" + username + '\'' + ", operation='"
            + operation + '\'' + ", time=" + time + ", method='" + method + '\'' + ", params='" + params + '\''
            + ", ip='" + ip + '\'' + ", gmtCreate=" + gmtCreate + '}';
    }
}
