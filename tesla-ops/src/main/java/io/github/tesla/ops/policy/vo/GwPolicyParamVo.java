package io.github.tesla.ops.policy.vo;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @ClassName GwPolicyParamVo
 * @Description Policy param vo
 * @Author zhouchao
 * @Date 2018/12/10 21:30
 * @Version 1.0
 **/
public class GwPolicyParamVo implements Serializable {
    private static final long serialVersionUID = -5334060107026490553L;
    private RateLimitVo rateLimit = new RateLimitVo();
    private QuotaVo quota = new QuotaVo();
    private List<String> accessControls = Lists.newArrayList();

    public List<String> getAccessControls() {
        return accessControls;
    }

    public QuotaVo getQuota() {
        return quota;
    }

    public RateLimitVo getRateLimit() {
        return rateLimit;
    }

    public void setAccessControls(List<String> accessControls) {
        this.accessControls = accessControls;
    }

    public void setQuota(QuotaVo quota) {
        this.quota = quota;
    }

    public void setRateLimit(RateLimitVo rateLimit) {
        this.rateLimit = rateLimit;
    }

}
