package io.github.tesla.auth.server.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Maps;

import io.github.tesla.auth.server.common.BaseController;
import io.github.tesla.auth.server.common.CommonResponse;
import io.github.tesla.auth.server.common.Log;
import io.github.tesla.auth.server.system.domain.DeptDO;
import io.github.tesla.auth.server.system.domain.Tree;
import io.github.tesla.auth.server.system.service.DeptService;

@RestController
@RequestMapping("/api/sys/dept")
public class DeptController extends BaseController {

    @Autowired
    private DeptService sysDeptService;

    @GetMapping
    @RequiresPermissions("sys:dept:dept")
    public List<DeptDO> list() {
        return sysDeptService.list(Maps.newHashMap());
    }

    @Log("保存")
    @PostMapping
    @RequiresPermissions("sys:dept:add")
    public CommonResponse save(DeptDO sysDept) {
        return sysDeptService.save(sysDept) ? CommonResponse.ok() : CommonResponse.error();
    }

    @Log("修改")
    @PutMapping
    @RequiresPermissions("sys:dept:edit")
    public CommonResponse update(DeptDO sysDept) {
        return sysDeptService.update(sysDept) ? CommonResponse.ok() : CommonResponse.error();
    }

    @Log("删除")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:dept:remove")
    public CommonResponse remove(@PathVariable("id") Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("parentId", id);
        if (sysDeptService.count(map) > 0) {
            return CommonResponse.error("包含下级部门,不允许修改");
        }
        if (sysDeptService.checkDeptHasUser(id)) {
            if (sysDeptService.remove(id)) {
                return CommonResponse.ok();
            }
        } else {
            return CommonResponse.error("部门包含用户,不允许修改");
        }
        return CommonResponse.error();
    }

    @Log("批量删除")
    @DeleteMapping
    @RequiresPermissions("sys:dept:batchRemove")
    public CommonResponse remove(@RequestParam("ids[]") Long[] deptIds) {
        return sysDeptService.batchRemove(deptIds) ? CommonResponse.ok() : CommonResponse.error();
    }

    @GetMapping("/tree")
    public Tree<DeptDO> tree() {
        return sysDeptService.getTree();
    }

}
