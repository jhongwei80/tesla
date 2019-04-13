package io.github.tesla.ops.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.MenuDO;

/**
 * 菜单管理
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 09:45:09
 */
@Mapper
public interface MenuDao {

    int batchRemove(Long[] menuIds);

    int count(Map<String, Object> map);

    MenuDO get(Long menuId);

    List<MenuDO> list(Map<String, Object> map);

    List<MenuDO> listMenuByUserId(Long id);

    List<String> listUserPerms(Long id);

    int remove(Long menuId);

    int removeByParent(Long parentId);

    int save(MenuDO menu);

    int update(MenuDO menu);
}
