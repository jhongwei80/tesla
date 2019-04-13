package io.github.tesla.ops.system.domain;

public class UserRoleDO {
    private Long id;
    private Long userId;
    private Long roleId;

    public Long getId() {
        return id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserRoleDO{" + "id=" + id + ", userId=" + userId + ", roleId=" + roleId + '}';
    }
}
