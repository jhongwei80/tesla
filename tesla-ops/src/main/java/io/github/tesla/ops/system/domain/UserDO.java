package io.github.tesla.ops.system.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class UserDO implements Serializable {
    private static final long serialVersionUID = 1L;

    // 用户ID
    private Long userId;
    // 用户名
    private String username;
    // 用户真实姓名
    private String name;
    // 密码
    private String password;
    // 部门
    private Long deptId;
    private String deptName;
    // 邮箱
    private String email;
    // 手机号
    private String mobile;
    /**
     * 状态 0:禁用，1:正常
     */
    private Integer status;
    // 创建用户id
    private Long userIdCreate;
    // 创建时间
    private Date gmtCreate;
    // 修改时间
    private Date gmtModified;
    // 角色
    private List<Long> roleIds;

    public Long getDeptId() {
        return deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    /**
     * 获取：邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 获取：创建时间
     */
    public Date getGmtCreate() {
        return gmtCreate;
    }

    /**
     * 获取：修改时间
     */
    public Date getGmtModified() {
        return gmtModified;
    }

    /**
     * 获取：手机号
     */
    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }

    /**
     * 获取：密码
     */
    public String getPassword() {
        return password;
    }

    public List<Long> getroleIds() {
        return roleIds;
    }

    /**
     * 获取：状态 0:禁用，1:正常
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 获取：
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 获取：创建用户id
     */
    public Long getUserIdCreate() {
        return userIdCreate;
    }

    /**
     * 获取：用户名
     */
    public String getUsername() {
        return username;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * 设置：邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 设置：创建时间
     */
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    /**
     * 设置：修改时间
     */
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    /**
     * 设置：手机号
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 设置：密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public void setroleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }

    /**
     * 设置：状态 0:禁用，1:正常
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 设置：
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 设置：创建用户id
     */
    public void setUserIdCreate(Long userIdCreate) {
        this.userIdCreate = userIdCreate;
    }

    /**
     * 设置：用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "UserDO{" + "userId=" + userId + ", username='" + username + '\'' + ", name='" + name + '\''
            + ", password='" + password + '\'' + ", deptId=" + deptId + ", deptName='" + deptName + '\'' + ", email='"
            + email + '\'' + ", mobile='" + mobile + '\'' + ", status=" + status + ", userIdCreate=" + userIdCreate
            + ", gmtCreate=" + gmtCreate + ", gmtModified=" + gmtModified + ", roleIds=" + roleIds + '}';
    }
}
