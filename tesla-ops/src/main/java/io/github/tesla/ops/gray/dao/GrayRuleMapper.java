package io.github.tesla.ops.gray.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.tesla.ops.gray.domain.GrayRuleDO;

@Mapper
public interface GrayRuleMapper extends BaseMapper<GrayRuleDO> {

}
