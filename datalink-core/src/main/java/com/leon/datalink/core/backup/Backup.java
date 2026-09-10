package com.leon.datalink.core.backup;

import com.leon.datalink.core.serializer.ProtostuffSerializable;

public class Backup implements ProtostuffSerializable {

    private String backupId;

    private String backupName;

    private String createTime;

    public String getBackupId() {
        return backupId;
    }

    public Backup setBackupId(String backupId) {
        this.backupId = backupId;
        return this;
    }

    public String getBackupName() {
        return backupName;
    }

    public Backup setBackupName(String backupName) {
        this.backupName = backupName;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Backup setCreateTime(String time) {
        this.createTime = time;
        return this;
    }
}
