package io.github.tesla.ops.gray.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.tesla.ops.gray.domain.GrayPolicyDO;

@Mapper
public interface GrayPolicyMapper extends BaseMapper<GrayPolicyDO> {

    @Select("select * from T_GRAY_POLICY where PLAN_ID=#{planId} and CONSUMER_SERVICE not in (select PROVIDER_SERVICE from T_GRAY_POLICY where PLAN_ID=#{planId})")
    List<GrayPolicyDO> findRootNodes(Long planId);

}
