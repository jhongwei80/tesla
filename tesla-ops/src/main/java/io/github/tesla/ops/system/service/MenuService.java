package io.github.tesla.ops.system.service;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;

import io.github.tesla.ops.system.dao.MenuDao;
import io.github.tesla.ops.system.dao.RoleMenuDao;
import io.github.tesla.ops.system.domain.MenuDO;
import io.github.tesla.ops.system.domain.Tree;
import io.github.tesla.ops.utils.BuildTree;

@Service
public class MenuService {

    @Autowired
    private MenuDao menuMapper;

    @Autowired
    private RoleMenuDao roleMenuMapper;

    @Cacheable

    public Tree<MenuDO> getSysMenuTree(Long id) {
        List<Tree<MenuDO>> trees = new ArrayList<Tree<MenuDO>>();
        List<MenuDO> menuDOs = menuMapper.listMenuByUserId(id);
        for (MenuDO sysMenuDO : menuDOs) {
            Tree<MenuDO> tree = new Tree<MenuDO>();
            tree.setId(sysMenuDO.getMenuId().toString());
            tree.setParentId(sysMenuDO.getParentId().toString());
            tree.setText(sysMenuDO.getName());
            Map<String, Object> attributes = new HashMap<>(16);
            attributes.put("url", sysMenuDO.getUrl());
            attributes.put("icon", sysMenuDO.getIcon());
            tree.setAttributes(attributes);
            trees.add(tree);
        }
        return BuildTree.build(trees);
    }

    public List<MenuDO> list() {
        return menuMapper.list(Maps.newHashMap());
    }

    public boolean remove(Long id) {
        menuMapper.removeByParent(id);
        return menuMapper.remove(id) == 1;
    }

    @Transactional(rollbackFor = Exception.class)

    public boolean batchRemove(Long[] ids) {
        return menuMapper.batchRemove(ids) > 0;
    }

    @Transactional(rollbackFor = Exception.class)

    public boolean save(MenuDO menu) {
        if (StringUtils.isEmpty(menu.getPerms())) {
            menu.setPerms(null);
        }
        return menuMapper.save(menu) == 1;
    }

    @Transactional(rollbackFor = Exception.class)

    public boolean update(MenuDO menu) {
        if (StringUtils.isEmpty(menu.getPerms())) {
            menu.setPerms(null);
        }
        menu.setGmtModified(new Date());
        return menuMapper.update(menu) == 1;
    }

    public MenuDO get(Long id) {
        return menuMapper.get(id);
    }

    public Tree<MenuDO> getTree() {
        List<Tree<MenuDO>> trees = new ArrayList<Tree<MenuDO>>();
        List<MenuDO> menuDOs = menuMapper.list(new HashMap<>(16));
        for (MenuDO sysMenuDO : menuDOs) {
            Tree<MenuDO> tree = new Tree<MenuDO>();
            tree.setId(sysMenuDO.getMenuId().toString());
            tree.setParentId(sysMenuDO.getParentId().toString());
            tree.setText(sysMenuDO.getName());
            trees.add(tree);
        }
        return BuildTree.build(trees);
    }

    public Tree<MenuDO> getTree(Long id) {
        List<MenuDO> menus = menuMapper.list(new HashMap<String, Object>(16));
        List<Long> menuIds = roleMenuMapper.listMenuIdByRoleId(id);
        List<Long> temp = menuIds;
        for (MenuDO menu : menus) {
            if (temp.contains(menu.getParentId())) {
                menuIds.remove(menu.getParentId());
            }
        }
        List<Tree<MenuDO>> trees = new ArrayList<Tree<MenuDO>>();
        List<MenuDO> menuDOs = menuMapper.list(new HashMap<String, Object>(16));
        for (MenuDO sysMenuDO : menuDOs) {
            Tree<MenuDO> tree = new Tree<MenuDO>();
            tree.setId(sysMenuDO.getMenuId().toString());
            tree.setParentId(sysMenuDO.getParentId().toString());
            tree.setText(sysMenuDO.getName());
            Map<String, Object> state = new HashMap<>(16);
            Long menuId = sysMenuDO.getMenuId();
            if (menuIds.contains(menuId)) {
                state.put("selected", true);
            } else {
                state.put("selected", false);
            }
            tree.setState(state);
            trees.add(tree);
        }
        return BuildTree.build(trees);
    }

    public List<Tree<MenuDO>> listMenuTree(Long id) {
        List<Tree<MenuDO>> trees = new ArrayList<Tree<MenuDO>>();
        List<MenuDO> menuDOs = menuMapper.listMenuByUserId(id);
        for (MenuDO sysMenuDO : menuDOs) {
            Tree<MenuDO> tree = new Tree<MenuDO>();
            tree.setId(sysMenuDO.getMenuId().toString());
            tree.setParentId(sysMenuDO.getParentId().toString());
            tree.setText(sysMenuDO.getName());
            Map<String, Object> attributes = new HashMap<>(16);
            attributes.put("url", sysMenuDO.getUrl());
            attributes.put("icon", sysMenuDO.getIcon());
            tree.setAttributes(attributes);
            trees.add(tree);
        }
        return BuildTree.buildList(trees, "0");
    }

}
