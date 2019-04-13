package io.github.tesla.ops.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.RoleDO;

/**
 * 角色
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-02 20:24:47
 */
@Mapper
public interface RoleDao {

    int batchRemove(Long[] roleIds);

    int count(Map<String, Object> map);

    RoleDO get(Long roleId);

    List<RoleDO> list(Map<String, Object> map);

    int remove(Long roleId);

    int save(RoleDO role);

    int update(RoleDO role);
}
