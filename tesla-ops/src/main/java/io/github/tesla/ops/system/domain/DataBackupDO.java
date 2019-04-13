package io.github.tesla.ops.system.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class DataBackupDO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String operatingTable;
    private String operatingType;
    private String tableData;
    private Timestamp operatingDate;
    private String operatingUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOperatingTable() {
        return operatingTable;
    }

    public void setOperatingTable(String operatingTable) {
        this.operatingTable = operatingTable;
    }

    public String getOperatingType() {
        return operatingType;
    }

    public void setOperatingType(String operatingType) {
        this.operatingType = operatingType;
    }

    public String getTableData() {
        return tableData;
    }

    public void setTableData(String tableData) {
        this.tableData = tableData;
    }

    public Timestamp getOperatingDate() {
        return operatingDate;
    }

    public void setOperatingDate(Timestamp operatingDate) {
        this.operatingDate = operatingDate;
    }

    public String getOperatingUser() {
        return operatingUser;
    }

    public void setOperatingUser(String operatingUser) {
        this.operatingUser = operatingUser;
    }
}
