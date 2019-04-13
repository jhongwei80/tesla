package io.github.tesla.filter.waf.definition;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 15:21
 * @description:
 */
public class BlackCookieDefinition extends PluginDefinition {

    private List<String> blackCookies;

    public List<String> getBlackCookies() {
        return blackCookies;
    }

    public void setBlackCookies(List<String> blackCookies) {
        this.blackCookies = blackCookies;
    }

    @Override
    public String validate(String paramJson) {
        if (StringUtils.isBlank(paramJson)) {
            return null;
        }
        blackCookies = JsonUtils.fromJson(paramJson, List.class);
        return JsonUtils.serializeToJson(this);
    }
}
