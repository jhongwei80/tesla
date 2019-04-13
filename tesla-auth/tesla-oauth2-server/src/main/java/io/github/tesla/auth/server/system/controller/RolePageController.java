package io.github.tesla.auth.server.system.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.tesla.auth.server.common.BaseController;
import io.github.tesla.auth.server.common.Log;
import io.github.tesla.auth.server.system.domain.RoleDO;
import io.github.tesla.auth.server.system.service.RoleService;

@RequestMapping("/sys/role")
@Controller
public class RolePageController extends BaseController {
    private final String prefix = "system/role";

    @Autowired
    private RoleService roleService;

    @RequiresPermissions("sys:role:role")
    @GetMapping()
    public String role() {
        return prefix + "/role";
    }

    @Log("添加角色")
    @RequiresPermissions("sys:role:add")
    @GetMapping("/add")
    public String add() {
        return prefix + "/add";
    }

    @Log("编辑角色")
    @RequiresPermissions("sys:role:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        RoleDO roleDO = roleService.get(id);
        model.addAttribute("role", roleDO);
        return prefix + "/edit";
    }

}
