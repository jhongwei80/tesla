package io.github.tesla.filter.endpoint.definition;

import com.google.common.base.Preconditions;

import io.github.tesla.common.dto.EndpointDTO;
import io.github.tesla.common.dto.ServiceDTO;
import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class CacheResultDefinition extends PluginDefinition {

    // 缓存失效时间，单位秒 <=0时 缓存不失效
    private Long expireSecond;

    public Long getExpireSecond() {
        return expireSecond;
    }

    public void setExpireSecond(Long expireSecond) {
        this.expireSecond = expireSecond;
    }

    @Override
    public String validate(String paramJson, ServiceDTO serviceDTO, EndpointDTO endpointDTO) {
        CacheResultDefinition definition = JsonUtils.fromJson(paramJson, CacheResultDefinition.class);
        Preconditions.checkArgument(definition.expireSecond != null, "缓存失效时间不可为空");
        return paramJson;
    }
}
