package io.github.tesla.ops.system.domain;

import java.sql.Timestamp;
import java.util.List;

public class RoleDO {

    private Long roleId;
    private String roleName;
    private String roleSign;
    private String remark;
    private Long userIdCreate;
    private Timestamp gmtCreate;
    private Timestamp gmtModified;
    private List<Long> menuIds;
    private String menuIdStr;

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public List<Long> getMenuIds() {
        return menuIds;
    }

    public String getMenuIdStr() {
        return menuIdStr;
    }

    public String getRemark() {
        return remark;
    }

    public Long getRoleId() {
        return roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getRoleSign() {
        return roleSign;
    }

    public Long getUserIdCreate() {
        return userIdCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setMenuIds(List<Long> menuIds) {
        this.menuIds = menuIds;
    }

    public void setMenuIdStr(String menuIds) {
        this.menuIdStr = menuIds;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setRoleSign(String roleSign) {
        this.roleSign = roleSign;
    }

    public void setUserIdCreate(Long userIdCreate) {
        this.userIdCreate = userIdCreate;
    }

    @Override
    public String toString() {
        return "RoleDO{" + "roleId=" + roleId + ", roleName='" + roleName + '\'' + ", roleSign='" + roleSign + '\''
            + ", remark='" + remark + '\'' + ", userIdCreate=" + userIdCreate + ", gmtCreate=" + gmtCreate
            + ", gmtModified=" + gmtModified + ", menuIds=" + menuIds + '}';
    }
}
