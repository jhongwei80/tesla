package io.github.tesla.auth.server.system.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.tesla.auth.server.system.dao.RoleDao;
import io.github.tesla.auth.server.system.dao.RoleMenuDao;
import io.github.tesla.auth.server.system.dao.UserDao;
import io.github.tesla.auth.server.system.dao.UserRoleDao;
import io.github.tesla.auth.server.system.domain.RoleDO;
import io.github.tesla.auth.server.system.domain.RoleMenuDO;

@Service
public class RoleService {

    public static final String ROLE_ALL_KEY = "\"role_all\"";

    public static final String CACHE_NAME = "role";

    @Autowired
    RoleDao roleMapper;
    @Autowired
    RoleMenuDao roleMenuMapper;
    @Autowired
    UserDao userMapper;
    @Autowired
    UserRoleDao userRoleMapper;

    public List<RoleDO> list() {
        List<RoleDO> roles = roleMapper.list(new HashMap<>(16));
        return roles;
    }

    public List<RoleDO> list(Long userId) {
        List<Long> rolesIds = userRoleMapper.listRoleId(userId);
        List<RoleDO> roles = roleMapper.list(new HashMap<>(16));
        for (RoleDO roleDO : roles) {
            roleDO.setRoleSign("false");
            for (Long roleId : rolesIds) {
                if (Objects.equals(roleDO.getRoleId(), roleId)) {
                    roleDO.setRoleSign("true");
                    break;
                }
            }
        }
        return roles;
    }

    @CacheEvict(value = CACHE_NAME, key = ROLE_ALL_KEY)
    @Transactional

    public int save(RoleDO role) {
        int count = roleMapper.save(role);
        List<Long> menuIds = role.getMenuIds();
        Long roleId = role.getRoleId();
        List<RoleMenuDO> rms = new ArrayList<>();
        for (Long menuId : menuIds) {
            if (menuId < 0) {
                continue;
            }
            RoleMenuDO rmDo = new RoleMenuDO();
            rmDo.setRoleId(roleId);
            rmDo.setMenuId(menuId);
            rms.add(rmDo);
        }
        roleMenuMapper.removeByRoleId(roleId);
        if (rms.size() > 0) {
            roleMenuMapper.batchSave(rms);
        }
        return count;
    }

    @CacheEvict(value = CACHE_NAME, key = ROLE_ALL_KEY)
    @Transactional

    public int remove(Long id) {
        int count = roleMapper.remove(id);
        roleMenuMapper.removeByRoleId(id);
        return count;
    }

    public RoleDO get(Long id) {
        return roleMapper.get(id);
    }

    @CacheEvict(value = CACHE_NAME, key = ROLE_ALL_KEY)

    public int update(RoleDO role) {
        int r = roleMapper.update(role);
        List<Long> menuIds = role.getMenuIds();
        Long roleId = role.getRoleId();
        roleMenuMapper.removeByRoleId(roleId);
        List<RoleMenuDO> rms = new ArrayList<>();
        for (Long menuId : menuIds) {
            if (menuId < 0) {
                continue;
            }
            RoleMenuDO rmDo = new RoleMenuDO();
            rmDo.setRoleId(roleId);
            rmDo.setMenuId(menuId);
            rms.add(rmDo);
        }
        if (rms.size() > 0) {
            roleMenuMapper.batchSave(rms);
        }
        return r;
    }

    public int batchremove(Long[] ids) {
        int r = roleMapper.batchRemove(ids);
        roleMenuMapper.batchRemove(ids);
        return r;
    }

}
