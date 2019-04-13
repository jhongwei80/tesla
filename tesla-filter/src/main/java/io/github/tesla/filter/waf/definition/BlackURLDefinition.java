package io.github.tesla.filter.waf.definition;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.github.tesla.filter.service.definition.PluginDefinition;
import io.github.tesla.filter.utils.JsonUtils;

/**
 * @ClassName BlackURLDefinition
 * @Description Black URL pattern list
 * @Author zhouchao
 * @Date 2018/11/28 15:32
 * @Version 1.0
 **/
public class BlackURLDefinition extends PluginDefinition {
    private List<String> blackURLs;

    public List<String> getBlackURLs() {
        return blackURLs;
    }

    public void setBlackURLs(List<String> blackURLs) {
        this.blackURLs = blackURLs;
    }

    @Override
    public String validate(String paramJson) {
        if (StringUtils.isBlank(paramJson)) {
            return null;
        }
        blackURLs = JsonUtils.fromJson(paramJson, List.class);
        return JsonUtils.serializeToJson(this);
    }
}
