package io.github.tesla.ops.system.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.LogDO;

/**
 * 系统日志
 * 
 * @author chglee
 * @email 1992lcg@163.com
 * @date 2017-10-03 15:45:42
 */
@Mapper
public interface LogDao {

    int batchRemove(Long[] ids);

    int count(Map<String, Object> map);

    LogDO get(Long id);

    List<LogDO> list(Map<String, Object> map);

    int remove(Long id);

    int save(LogDO log);

    int update(LogDO log);
}
