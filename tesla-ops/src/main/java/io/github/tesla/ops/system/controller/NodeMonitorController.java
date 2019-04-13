package io.github.tesla.ops.system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.netflix.discovery.shared.Application;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.Constant;
import io.github.tesla.ops.common.Pageable;
import io.github.tesla.ops.system.vo.EurekaAppVo;
import io.github.tesla.ops.utils.EurekaClientUtil;

@RestController
@RequestMapping("/api/sys/nodemonitor")
public class NodeMonitorController extends BaseController {

    @GetMapping
    public Pageable list() {
        List<EurekaAppVo> eurekaAppVos = Lists.newArrayList();
        Application gatewayApp = EurekaClientUtil.getApplication(Constant.GATEWAY_APP_NAME);
        if (gatewayApp != null) {
            gatewayApp.getInstances().stream().forEach(instanceInfo -> {
                EurekaAppVo eurekaAppVo = new EurekaAppVo();
                eurekaAppVo.setApp(instanceInfo.getAppName());
                eurekaAppVo.setHostName(instanceInfo.getHostName());
                eurekaAppVo.setInstanceId(instanceInfo.getInstanceId());
                eurekaAppVo.setIpAddr(instanceInfo.getIPAddr());
                eurekaAppVo.setStatus(instanceInfo.getStatus().name());
                eurekaAppVo.setManagementPort(instanceInfo.getMetadata().get("management.port"));
                eurekaAppVos.add(eurekaAppVo);
            });
        }
        return new Pageable(eurekaAppVos, eurekaAppVos.size());
    }

}
