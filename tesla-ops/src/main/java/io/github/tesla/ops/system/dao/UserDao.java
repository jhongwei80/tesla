package io.github.tesla.ops.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.UserDO;

/**
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 09:45:11
 */
@Mapper
public interface UserDao {

    int batchRemove(Long[] userIds);

    int count(Map<String, Object> map);

    UserDO get(Long userId);

    List<UserDO> list(Map<String, Object> map);

    Long[] listAllDept();

    int remove(Long userId);

    int save(UserDO user);

    int update(UserDO user);
}
