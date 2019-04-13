package io.github.tesla.ops.system.service;

import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.annotation.TableName;

import io.github.tesla.filter.utils.JsonUtils;
import io.github.tesla.ops.common.OperatingTypeEnum;
import io.github.tesla.ops.system.dao.DataBackupDao;
import io.github.tesla.ops.system.domain.DataBackupDO;

@Service
public class DataBackupService {

    private final static Logger logger = LoggerFactory.getLogger(DataBackupService.class);

    @Autowired
    private DataBackupDao dataBackupDao;

    public void backup(Object data, OperatingTypeEnum operatingType) {
        DataBackupDO dataBackupDO = new DataBackupDO();
        dataBackupDO.setTableData(JsonUtils.serializeToJson(data));

        dataBackupDO.setOperatingType(operatingType.getCode());
        dataBackupDO.setOperatingUser(SecurityUtils.getSubject().getPrincipals().getRealmNames().iterator().next());
        String tableName;
        if (data.getClass().getAnnotation(TableName.class) != null) {
            tableName = data.getClass().getAnnotation(TableName.class).value();
        } else {
            tableName = data.getClass().getSimpleName();
        }
        dataBackupDO.setOperatingTable(tableName);
        logger.info("DataBackup: " + JsonUtils.serializeToJson(dataBackupDO));
        dataBackupDao.save(dataBackupDO);
    }

}
