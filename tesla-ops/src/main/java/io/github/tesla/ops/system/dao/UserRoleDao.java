package io.github.tesla.ops.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.UserRoleDO;

/**
 * 用户与角色对应关系
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 11:08:59
 */
@Mapper
public interface UserRoleDao {

    int batchRemove(Long[] ids);

    int batchRemoveByUserId(Long[] ids);

    int batchSave(List<UserRoleDO> list);

    int count(Map<String, Object> map);

    UserRoleDO get(Long id);

    List<UserRoleDO> list(Map<String, Object> map);

    List<Long> listRoleId(Long userId);

    int remove(Long id);

    int removeByUserId(Long userId);

    int save(UserRoleDO userRole);

    int update(UserRoleDO userRole);
}
