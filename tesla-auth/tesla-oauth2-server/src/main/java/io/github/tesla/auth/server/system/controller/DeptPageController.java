package io.github.tesla.auth.server.system.controller;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.tesla.auth.server.common.BaseController;
import io.github.tesla.auth.server.common.Constant;
import io.github.tesla.auth.server.system.domain.DeptDO;
import io.github.tesla.auth.server.system.service.DeptService;

@Controller
@RequestMapping("/sys/dept")
public class DeptPageController extends BaseController {
    private String prefix = "system/dept";

    @Autowired
    private DeptService sysDeptService;

    @GetMapping()
    @RequiresPermissions("sys:dept:dept")
    public String dept() {
        return prefix + "/dept";
    }

    @GetMapping("/add/{pId}")
    @RequiresPermissions("sys:dept:add")
    public String add(@PathVariable("pId") Long pId, Model model) {
        model.addAttribute("pId", pId);
        if (pId == 0) {
            model.addAttribute("pName", "总部门");
        } else {
            model.addAttribute("pName", sysDeptService.get(pId).getName());
        }
        return prefix + "/add";
    }

    @GetMapping("/edit/{deptId}")
    @RequiresPermissions("sys:dept:edit")
    public String edit(@PathVariable("deptId") Long deptId, Model model) {
        DeptDO sysDept = sysDeptService.get(deptId);
        model.addAttribute("sysDept", sysDept);
        if (Constant.DEPT_ROOT_ID.equals(sysDept.getParentId())) {
            model.addAttribute("parentDeptName", "无");
        } else {
            DeptDO parDept = sysDeptService.get(sysDept.getParentId());
            model.addAttribute("parentDeptName", parDept.getName());
        }
        return prefix + "/edit";
    }

    @GetMapping("/treeView")
    public String treeView() {
        return prefix + "/deptTree";
    }

}
