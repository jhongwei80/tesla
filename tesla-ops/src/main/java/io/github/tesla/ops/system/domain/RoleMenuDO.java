package io.github.tesla.ops.system.domain;

public class RoleMenuDO {
    private Long id;
    private Long roleId;
    private Long menuId;

    public Long getId() {
        return id;
    }

    public Long getMenuId() {
        return menuId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Override
    public String toString() {
        return "RoleMenuDO{" + "id=" + id + ", roleId=" + roleId + ", menuId=" + menuId + '}';
    }
}
