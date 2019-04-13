package io.github.tesla.ops.system.dao;

import org.apache.ibatis.annotations.Mapper;

import io.github.tesla.ops.system.domain.DataBackupDO;

@Mapper
public interface DataBackupDao {

    int save(DataBackupDO dataBackupDO);

}
