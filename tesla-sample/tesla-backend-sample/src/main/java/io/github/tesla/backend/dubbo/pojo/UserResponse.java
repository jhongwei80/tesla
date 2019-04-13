package io.github.tesla.backend.dubbo.pojo;

import java.io.Serializable;

public class UserResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;

    private String mobile;

    private String idNo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdNo() {
        return idNo;
    }

    public void setIdNo(String idNo) {
        this.idNo = idNo;
    }

}
