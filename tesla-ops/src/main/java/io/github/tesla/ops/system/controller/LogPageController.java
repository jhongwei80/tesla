package io.github.tesla.ops.system.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/sys/log")
@Controller
public class LogPageController {

    private String prefix = "system/log";

    @GetMapping()
    @RequiresPermissions("sys:monitor:log")
    public String log() {
        return prefix + "/log";
    }

    @GetMapping("/run")
    @RequiresPermissions("sys:monitor:run")
    public String run() {
        return prefix + "/monitor";
    }

}
