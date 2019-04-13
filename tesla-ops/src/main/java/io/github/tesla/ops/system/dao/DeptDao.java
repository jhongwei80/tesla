package io.github.tesla.ops.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.DeptDO;

/**
 * 部门管理
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 15:35:39
 */
@Mapper
public interface DeptDao {

    int batchRemove(Long[] deptIds);

    int count(Map<String, Object> map);

    DeptDO get(Long deptId);

    int getDeptUserNumber(Long deptId);

    List<DeptDO> list(Map<String, Object> map);

    Long[] listParentDept();

    int remove(Long deptId);

    int save(DeptDO dept);

    int update(DeptDO dept);
}
