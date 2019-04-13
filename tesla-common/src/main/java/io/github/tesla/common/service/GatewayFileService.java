package io.github.tesla.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tesla.common.dao.GatewayFileMapper;
import io.github.tesla.common.domain.GatewayFileDO;

/**
 * 从数据库加载自定义Jar包插件
 * 
 * @author liushiming
 * @date 2018/12/04
 */
@Service
public class GatewayFileService {

    @Autowired
    private GatewayFileMapper gatewayFileMapper;

    public List<GatewayFileDO> loadEnabledFile() {
        List<GatewayFileDO> fileDOList = gatewayFileMapper.selectByMap(null);
        return fileDOList;
    }

}
