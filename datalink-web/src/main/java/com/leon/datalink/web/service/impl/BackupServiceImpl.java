package com.leon.datalink.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ClassUtil;
import com.leon.datalink.core.backup.Backup;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.storage.kv.FileKvStorage;
import com.leon.datalink.core.utils.*;
import com.leon.datalink.core.backup.BackupData;
import com.leon.datalink.web.service.BackupService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.leon.datalink.core.common.Constants.STORAGE_PATH;

/**
 * @ClassNameResourceManager
 * @Description
 * @Author Leon
 * @Date2022/4/2 15:03
 * @Version V1.0
 **/
@Service
public class BackupServiceImpl implements BackupService {

    /**
     * 备份列表
     */
    private final ConcurrentHashMap<String, Backup> backupList = new ConcurrentHashMap<>();

    /**
     * key value storage
     */
    private final KvStorage kvStorage;

    /**
     * file storage
     */
    private final KvStorage fileStorage;

    /**
     * 插件持久化路径
     */
    private final static String BACKUP_PATH = "/backup";

    public BackupServiceImpl() throws Exception {

        // init storage
        this.kvStorage = new DatalinkKvStorage(STORAGE_PATH + BACKUP_PATH);

        // init file storage
        this.fileStorage = new FileKvStorage(Constants.BACKUP_FILE_PATH);

        // read backup list form storage
        if (this.kvStorage.allKeys().size() <= 0) return;
        for (byte[] key : this.kvStorage.allKeys()) {
            String fileName = new String(key);
            byte[] value = this.kvStorage.get(key);
            Backup backup = JacksonUtils.toObj(value, Backup.class);
            backupList.put(fileName, backup);
        }

    }

    @Override
    public byte[] download(String fileName) throws KvStorageException {
        return this.fileStorage.get(fileName.getBytes());
    }

    /**
     * 上传并使用备份
     *
     * @param backupName
     * @param bytes
     * @throws KvStorageException
     */
    @Override
    public void upload(String backupName, byte[] bytes) throws KvStorageException {
        Backup backup = new Backup();
        backup.setBackupId(SnowflakeIdWorker.getId());
        backup.setBackupName(backupName);
        backup.setCreateTime(DateUtil.formatDateTime(new Date()));
        this.kvStorage.put(backup.getBackupId().getBytes(), JacksonUtils.toJsonBytes(backup));
        backupList.put(backup.getBackupId(), backup);
        this.fileStorage.put(backupName.getBytes(), bytes);
        this.doRecover(bytes);
    }

    /**
     * 恢复备份
     *
     * @param fileName
     * @throws KvStorageException
     */
    @Override
    public void recover(String fileName) throws KvStorageException {
        byte[] bytes = this.fileStorage.get(fileName.getBytes());
        this.doRecover(bytes);
    }

    // 恢复备份
    private void doRecover(byte[] bytes) {
        Map<String, Object> map = JacksonUtils.toMapObj(bytes, String.class, Object.class);
        String version = (String) map.get("version");
        if (!VersionUtils.version.equals(version)) return;
        Map<String, BackupData> backupDataMap = SpringBeanUtil.getApplicationContext().getBeansOfType(BackupData.class);
        backupDataMap.values().forEach(backupData -> {
            Object content = map.get(backupData.dataKey());
            if (null != content) {
                backupData.recoverBackup(JacksonUtils.toListObj(content, ClassUtil.getTypeArgument(backupData.getClass())));
            }
        });
    }


    @Override
    public Backup get(String backupId) {
        return backupList.get(backupId);
    }

    /**
     * 创建备份
     *
     * @param backup
     * @throws KvStorageException
     */
    @Override
    public void add(Backup backup) throws KvStorageException {
        Date date = new Date();
        String backupName = String.format("datalink_backup_%s.json", DateUtil.format(date, "yyyyMMdd_HHmmssSSS"));

        Map<String, Object> backupContent = new HashMap<>();
        backupContent.put("version", VersionUtils.version);
        Map<String, BackupData> backupDataMap = SpringBeanUtil.getApplicationContext().getBeansOfType(BackupData.class);
        backupDataMap.values().forEach(backupData -> backupContent.put(backupData.dataKey(), backupData.createBackup()));

        this.fileStorage.put(backupName.getBytes(), JacksonUtils.toJsonBytes(backupContent));

        backup.setBackupId(SnowflakeIdWorker.getId());
        backup.setBackupName(backupName);
        backup.setCreateTime(DateUtil.formatDateTime(date));
        this.kvStorage.put(backup.getBackupId().getBytes(), JacksonUtils.toJsonBytes(backup));
        backupList.put(backup.getBackupId(), backup);
    }

    @Override
    public void remove(String backupId) throws KvStorageException {
        Backup backup = this.get(backupId);
        this.kvStorage.delete(backupId.getBytes());
        this.fileStorage.delete(backup.getBackupName().getBytes());
        backupList.remove(backup.getBackupId());
    }

    @Override
    public void update(Backup backup) throws KvStorageException {
        this.kvStorage.put(backup.getBackupId().getBytes(), JacksonUtils.toJsonBytes(backup));
        backupList.put(backup.getBackupId(), backup);
    }

    @Override
    public List<Backup> list(Backup backup) {
        Stream<Backup> stream = backupList.values().stream();
        if (null != backup) {
            if (!StringUtils.isEmpty(backup.getBackupName())) {
                stream = stream.filter(s -> s.getBackupName().contains(backup.getBackupName()));
            }
        }
        return CollectionUtil.reverse(stream.sorted(Comparator.comparingLong(item -> Long.parseLong(item.getBackupId()))).collect(Collectors.toList()));
    }

    @Override
    public int getCount(Backup backup) {
        return this.list(backup).size();
    }


}
