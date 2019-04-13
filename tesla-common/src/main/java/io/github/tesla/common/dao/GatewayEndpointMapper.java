package io.github.tesla.common.dao;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.tesla.common.domain.GatewayEndpointDO;

/**
 * @author: zhangzhiping
 * @date: 2018/11/20 13:56
 * @description:
 */
@Mapper
public interface GatewayEndpointMapper extends BaseMapper<GatewayEndpointDO> {

}
