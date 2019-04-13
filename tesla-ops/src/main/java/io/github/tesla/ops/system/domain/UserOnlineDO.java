package io.github.tesla.ops.system.domain;

import java.util.Date;

public class UserOnlineDO {

    /**
     */
    private String id;

    private String userId;

    private String username;

    /**
     * 用户主机地址
     */
    private String host;

    /**
     * 用户登录时系统IP
     */
    private String systemHost;

    /**
     * 用户浏览器类型
     */
    private String userAgent;

    /**
     * 在线状态
     */
    private String status = "on_line";

    /**
     * session创建时间
     */
    private Date startTimestamp;
    /**
     * session最后访问时间
     */
    private Date lastAccessTime;

    /**
     * 超时时间
     */
    private Long timeout;

    /**
     * 备份的当前用户会话
     */
    private String onlineSession;

    public String getHost() {
        return host;
    }

    public String getId() {
        return id;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public String getOnlineSession() {
        return onlineSession;
    }

    public Date getStartTimestamp() {
        return startTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getSystemHost() {
        return systemHost;
    }

    public Long getTimeout() {
        return timeout;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public void setOnlineSession(String onlineSession) {
        this.onlineSession = onlineSession;
    }

    public void setStartTimestamp(Date startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSystemHost(String systemHost) {
        this.systemHost = systemHost;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
