package io.github.tesla.ops.system.controller;

import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import io.github.tesla.ops.common.BaseController;
import io.github.tesla.ops.common.CommonResponse;
import io.github.tesla.ops.common.Log;
import io.github.tesla.ops.system.domain.MenuDO;
import io.github.tesla.ops.system.domain.Tree;
import io.github.tesla.ops.system.service.MenuService;

@RestController
@RequestMapping("/api/sys/menu")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    @Log("批量删除菜单")
    @DeleteMapping
    @RequiresPermissions("sys:menu:batchRemove")
    public CommonResponse batchRemove(@RequestParam("ids[]") Long[] ids) {
        return menuService.batchRemove(ids) ? CommonResponse.ok() : CommonResponse.error("删除失败");
    }

    @GetMapping
    @RequiresPermissions("sys:menu:menu")
    public List<MenuDO> list() {
        return menuService.list();
    }

    @Log("删除菜单")
    @DeleteMapping("/{id}")
    @RequiresPermissions("sys:menu:remove")
    public CommonResponse remove(@PathVariable("id") Long id) {
        return menuService.remove(id) ? CommonResponse.ok() : CommonResponse.error("删除失败");
    }

    @Log("保存菜单")
    @PostMapping
    @RequiresPermissions("sys:menu:add")
    public CommonResponse save(MenuDO menu) {
        return menuService.save(menu) ? CommonResponse.ok() : CommonResponse.error("保存失败");
    }

    @GetMapping("/tree")
    @ResponseBody
    public Tree<MenuDO> tree() {
        return menuService.getTree();
    }

    @GetMapping("/tree/{roleId}")
    @ResponseBody
    public Tree<MenuDO> tree(@PathVariable("roleId") Long roleId) {
        return menuService.getTree(roleId);
    }

    @Log("更新菜单")
    @PutMapping
    @RequiresPermissions("sys:menu:edit")
    public CommonResponse update(MenuDO menu) {
        return menuService.update(menu) ? CommonResponse.ok() : CommonResponse.error("更新失败");
    }
}
