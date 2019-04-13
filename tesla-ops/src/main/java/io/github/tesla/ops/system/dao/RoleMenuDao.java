package io.github.tesla.ops.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.RoleMenuDO;

/**
 * 角色与菜单对应关系
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 11:08:59
 */
@Mapper
public interface RoleMenuDao {

    int batchRemove(Long[] ids);

    int batchSave(List<RoleMenuDO> list);

    int count(Map<String, Object> map);

    RoleMenuDO get(Long id);

    List<RoleMenuDO> list(Map<String, Object> map);

    List<Long> listMenuIdByRoleId(Long roleId);

    int remove(Long id);

    int removeByRoleId(Long roleId);

    int save(RoleMenuDO roleMenu);

    int update(RoleMenuDO roleMenu);
}
