package io.github.tesla.ops.system.vo;

import java.io.Serializable;

public class PasswdVo implements Serializable {
    private static final long serialVersionUID = -8359205188653743015L;

    private Long userId;

    private String username;

    private String password;

    private String newPasswd;

    private String confirmPasswd;

    public String getConfirmPasswd() {
        return confirmPasswd;
    }

    public String getNewPasswd() {
        return newPasswd;
    }

    public String getPassword() {
        return password;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public void setConfirmPasswd(String confirmPasswd) {
        this.confirmPasswd = confirmPasswd;
    }

    public void setNewPasswd(String newPasswd) {
        this.newPasswd = newPasswd;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
