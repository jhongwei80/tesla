package io.github.tesla.auth.server.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.auth.server.common.BaseController;
import io.github.tesla.auth.server.common.Constant;
import io.github.tesla.auth.server.common.Log;
import io.github.tesla.auth.server.system.domain.DeptDO;
import io.github.tesla.auth.server.system.domain.RoleDO;
import io.github.tesla.auth.server.system.domain.UserDO;
import io.github.tesla.auth.server.system.service.DeptService;
import io.github.tesla.auth.server.system.service.RoleService;
import io.github.tesla.auth.server.system.service.UserService;

@Controller
@RequestMapping("/sys/user")
public class UserPageController extends BaseController {
    private String prefix = "system/user";
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private DeptService deptService;

    @RequiresPermissions("sys:user:user")
    @GetMapping
    public String user(Model model) {
        Map<String, Object> param = new HashMap<>(1);
        param.put("parentId", Constant.DEPT_ROOT_ID);
        List<DeptDO> depts = deptService.list(param);
        model.addAttribute("depts", depts);
        return prefix + "/user";
    }

    @RequiresPermissions("sys:user:add")
    @Log("添加用户")
    @GetMapping("/add")
    public String add(Model model) {
        List<RoleDO> roles = roleService.list();
        model.addAttribute("roles", roles);
        return prefix + "/add";
    }

    @Log("编辑用户")
    @GetMapping("/edit/{id}")
    @RequiresPermissions("sys:user:edit")
    public String edit(Model model, @PathVariable("id") Long id) {
        UserDO userDO = userService.get(id);
        model.addAttribute("user", userDO);
        List<RoleDO> roles = roleService.list(id);
        model.addAttribute("roles", roles);
        return prefix + "/edit";
    }

    @PostMapping("/exit")
    @ResponseBody
    boolean exit(@RequestParam Map<String, Object> params) {
        // 存在，不通过，false
        return !userService.exit(params);
    }

    @Log("请求更改用户密码")
    @RequiresPermissions("sys:user:resetPwd")
    @GetMapping("/resetPwd/{userId}")
    public String resetPwd(@PathVariable("userId") Long userId, Model model) {
        UserDO userDO = userService.get(userId);
        model.addAttribute("user", userDO);
        return prefix + "/reset_pwd";
    }

    @GetMapping("/treeView")
    public String treeView() {
        return prefix + "/userTree";
    }

    @GetMapping("/personal")
    public String personal(Model model) {
        return prefix + "/personal";
    }

}
