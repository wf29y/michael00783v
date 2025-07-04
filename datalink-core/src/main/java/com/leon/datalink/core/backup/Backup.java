package com.leon.datalink.core.backup;

import java.io.Serializable;

public class Backup implements Serializable {
    private static final long serialVersionUID = 1895166087085804264L;

    private String backupId;

    private String backupName;

    private String createTime;

    public String getBackupId() {
        return backupId;
    }

    public void setBackupId(String backupId) {
        this.backupId = backupId;
    }

    public String getBackupName() {
        return backupName;
    }

    public void setBackupName(String backupName) {
        this.backupName = backupName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String time) {
        this.createTime = time;
    }
}
