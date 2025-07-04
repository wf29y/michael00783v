package com.leon.datalink.web.backup;

import com.leon.datalink.core.backup.Backup;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.service.BaseService;

public interface BackupService extends BaseService<Backup> {

    byte[] download(String fileName) throws KvStorageException;

    void upload(String fileName,byte[] file) throws KvStorageException;

    void recover(String fileName) throws KvStorageException;
}
