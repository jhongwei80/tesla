package io.github.tesla.filter.common.definition;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.support.enums.YesOrNoEnum;
import io.github.tesla.filter.utils.JsonUtils;

public class RateLimitDefinition extends PluginDefinition {

    private String appId;

    private long rate;

    private long perSeconds;

    private String enabled;

    public String getAppId() {
        return appId;
    }

    public String getEnabled() {
        return enabled;
    }

    public long getPerSeconds() {
        return perSeconds;
    }

    public long getRate() {
        return rate;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public void setPerSeconds(long perSeconds) {
        this.perSeconds = perSeconds;
    }

    public void setRate(long rate) {
        this.rate = rate;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO) {
        RateLimitDefinition definition = JsonUtils.fromJson(paramJson, RateLimitDefinition.class);
        if (YesOrNoEnum.YES.getCode().equals(definition.getEnabled())) {
            Preconditions.checkArgument(definition.getPerSeconds() > 0, "限流时间不可小于0");
            Preconditions.checkArgument(definition.getRate() > 0, "限流速度不可小于0");
        }
        definition.setAppId(serviceDTO.getServiceId());
        return JsonUtils.serializeToJson(definition);
    }

}
