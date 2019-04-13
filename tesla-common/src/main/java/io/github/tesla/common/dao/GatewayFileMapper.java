package io.github.tesla.common.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.tesla.common.domain.GatewayFileDO;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
@Mapper
public interface GatewayFileMapper extends BaseMapper<GatewayFileDO> {

}
