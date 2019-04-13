package io.github.tesla.ops.system.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.system.domain.RoleDO;
import io.github.tesla.ops.system.service.RoleService;

@RequestMapping("/api/sys/role")
@RestController
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @RequiresPermissions("sys:role:batchRemove")
    @Log("批量删除角色")
    @DeleteMapping()
    public CommonResponse batchRemove(@RequestParam("ids[]") Long[] ids) {
        int r = roleService.batchremove(ids);
        if (r > 0) {
            return CommonResponse.ok();
        }
        return CommonResponse.error();
    }

    @RequiresPermissions("sys:role:role")
    @GetMapping
    public List<RoleDO> list() {
        return roleService.list();
    }

    @Log("删除角色")
    @RequiresPermissions("sys:role:remove")
    @DeleteMapping("/{id}")
    public CommonResponse save(@PathVariable("id") Long id) {
        if (roleService.remove(id) > 0) {
            return CommonResponse.ok();
        } else {
            return CommonResponse.error("删除失败");
        }
    }

    @Log("保存角色")
    @RequiresPermissions("sys:role:add")
    @PostMapping()
    public CommonResponse save(RoleDO role) {
        Long userId = BaseController.getUserId();
        role.setUserIdCreate(userId);
        if (roleService.save(role) > 0) {
            return CommonResponse.ok();
        } else {
            return CommonResponse.error("保存失败");
        }
    }

    @Log("更新角色")
    @RequiresPermissions("sys:role:edit")
    @PutMapping()
    public CommonResponse update(RoleDO role) {
        Long userId = BaseController.getUserId();
        role.setUserIdCreate(userId);
        if (roleService.update(role) > 0) {
            return CommonResponse.ok();
        } else {
            return CommonResponse.error("保存失败");
        }
    }
}
