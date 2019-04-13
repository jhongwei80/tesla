package io.github.tesla.common.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import io.github.tesla.common.domain.GatewayServiceDO;

/**
 * @author: zhipingzhang
 * @date: 2018/11/20 11:03
 * @description:
 */
@Mapper
public interface GatewayServiceMapper extends BaseMapper<GatewayServiceDO> {
    @Select("select service_id,service_name,service_prefix from gateway_service")
    List<Map<String, String>> getServiceInfo();

    @Select("select service_id,service_prefix from gateway_service")
    List<Map<String, String>> getServicePrefixInfo();
}
