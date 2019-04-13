package io.github.tesla.auth.server.system.controller;

import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.auth.server.common.CommonResponse;
import io.github.tesla.auth.server.system.domain.LogDO;
import io.github.tesla.auth.server.system.domain.PageDO;
import io.github.tesla.auth.server.system.service.LogService;
import io.github.tesla.auth.server.utils.Query;

@RequestMapping("/sys/logapi")
@RestController
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping()
    @RequiresPermissions("sys:monitor:log")
    public PageDO<LogDO> list(@RequestParam Map<String, Object> params) {
        return logService.queryList(new Query(params));
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:monitor:log")
    public CommonResponse remove(@PathVariable("id") Long id) {
        return logService.remove(id) ? CommonResponse.ok() : CommonResponse.error();
    }

    @DeleteMapping()
    @RequiresPermissions("sys:monitor:log")
    public CommonResponse batchRemove(@RequestParam("ids[]") Long[] ids) {
        return logService.batchRemove(ids) ? CommonResponse.ok() : CommonResponse.error();
    }
}
