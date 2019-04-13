package io.github.tesla.ops.policy.vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @ClassName GwPolicyVo
 * @Description Gateway policy vo
 * @Author zhouchao
 * @Date 2018/12/6 18:09
 * @Version 1.0
 **/
public class GwPolicyVo implements Serializable {
    private static final long serialVersionUID = -5350573588345272155L;
    private Long id;

    private String policyName;

    private String policyDesc;

    private GwPolicyParamVo policyParamVo = new GwPolicyParamVo();

    private String policyParam;

    private Timestamp gmtCreate;

    private Timestamp gmtModified;

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public Timestamp getGmtModified() {
        return gmtModified;
    }

    public Long getId() {
        return id;
    }

    public String getPolicyDesc() {
        return policyDesc;
    }

    public String getPolicyName() {
        return policyName;
    }

    public String getPolicyParam() {
        return policyParam;
    }

    public GwPolicyParamVo getPolicyParamVo() {
        return policyParamVo;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public void setGmtModified(Timestamp gmtModified) {
        this.gmtModified = gmtModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPolicyDesc(String policyDesc) {
        this.policyDesc = policyDesc;
    }

    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    public void setPolicyParam(String policyParam) {
        this.policyParam = policyParam;
    }

    public void setPolicyParamVo(GwPolicyParamVo policyParamVo) {
        this.policyParamVo = policyParamVo;
    }
}
