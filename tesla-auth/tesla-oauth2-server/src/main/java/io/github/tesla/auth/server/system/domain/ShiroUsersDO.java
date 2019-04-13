package io.github.tesla.auth.server.system.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ShiroUsersDO implements Serializable {

    private static final long serialVersionUID = -356109547625692111L;

    private Long userId;

    private String username;

    private String password;

    private int status;

    private List<String> roles;

    public Long userId() {
        return userId;
    }

    public ShiroUsersDO userId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String username() {
        return username;
    }

    public ShiroUsersDO username(String username) {
        this.username = username;
        return this;
    }

    public String password() {
        return password;
    }

    public ShiroUsersDO password(String password) {
        this.password = password;
        return this;
    }

    public int status() {
        return this.status;
    }

    public ShiroUsersDO status(int status) {
        this.status = status;
        return this;
    }

    public List<String> roles() {
        return this.roles;
    }

    public ShiroUsersDO roles(List<String> roles) {
        this.roles = roles;
        return this;
    }

    public ShiroUsersDO roles(String role) {
        if (this.roles == null) {
            this.roles = new ArrayList<String>();
        }
        this.roles.add(role);
        return this;
    }

    @Override
    public String toString() {
        return "ShiroUsersDO [userId=" + userId + ", username=" + username + ", password=" + password + ", status="
            + status + ", roles=" + roles + "]";
    }

}
