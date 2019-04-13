package io.github.tesla.ops.gray.controller;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Sets;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.utils.EurekaClientUtil;

@RestController
@RequestMapping("/api/gray/eureka")
public class EurekaClientController extends BaseController {

    private String grayEnable = "gray.enable";
    private String grayUrl = "management.url";

    @GetMapping()
    public Set<String> list(@RequestParam Map<String, Object> params) {

        Set<String> appSet = Sets.newHashSet();
        EurekaClientUtil.getApplications().getRegisteredApplications().forEach(app -> {
            app.getInstances().stream().filter(instance -> MapUtils.getBooleanValue(instance.getMetadata(), grayEnable)
                && isNotBlank(MapUtils.getString(instance.getMetadata(), grayUrl))).forEach(instanceInfo -> {
                    appSet.add(instanceInfo.getAppName());
                });
        });
        return appSet;
    }

}
