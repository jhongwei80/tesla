package io.github.tesla.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.tesla.common.domain.GatewayPolicyDO;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
@Mapper
public interface GatewayPolicyMapper extends BaseMapper<GatewayPolicyDO> {
    @Select("select policy_name,policy_param from gateway_policy")
    List<Map<String, String>> getPolicyInfo();
}
