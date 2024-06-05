

package com.leon.datalink.core.storage;


import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.utils.TimerContext;
import com.leon.datalink.core.exception.ErrorCode;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.exception.DatalinkException;
import com.leon.datalink.core.exception.runtime.DatalinkRuntimeException;
import com.leon.datalink.core.storage.kv.MemoryKvStorage;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Kv storage implementation for datalink.
 *
 * @author Leon
 */
public class DatalinkKvStorage extends MemoryKvStorage {

    private static final String LOAD_SNAPSHOT = DatalinkKvStorage.class.getSimpleName() + ".snapshotLoad";

    private final String baseDir;

    private final KvStorage baseDirStorage;

    private final Map<String, KvStorage> mqttKvStorage;

    private boolean isSnapshotLoad = false;

    public boolean isSnapshotLoad() {
        return this.isSnapshotLoad;
    }

    public DatalinkKvStorage(final String baseDir) throws Exception {
        this.baseDir = baseDir;
        this.baseDirStorage = StorageFactory.createKvStorage(KvStorage.KvType.RocksDB, baseDir);
        this.mqttKvStorage = new ConcurrentHashMap<>(16);
    }


    
    @Override
    public byte[] get(byte[] key) throws KvStorageException {
        // First get the data from the memory Cache
        byte[] result = super.get(key);
        if (null == result) {
            try {
                KvStorage storage = createActualStorageIfAbsent(key);
                result = null == storage ? null : storage.get(key);
                if (null != result) {
                    super.put(key, result);
                }
            } catch (Exception e) {
                throw new KvStorageException(ErrorCode.KVStorageWriteError.getCode(),
                        "Get data failed, key: " + new String(key) + ", detail: " + e.getMessage(), e);
            }
        }
        return result;
    }
    
    @Override
    public Map<byte[], byte[]> batchGet(List<byte[]> keys) throws KvStorageException {
        Map<byte[], byte[]> result = new HashMap<>(keys.size());
        for (byte[] key : keys) {
            byte[] val = get(key);
            if (val != null) {
                result.put(key, val);
            }
        }
        return result;
    }
    
    @Override
    public void put(byte[] key, byte[] value) throws KvStorageException {
        try {
            KvStorage storage = createActualStorageIfAbsent(key);
            storage.put(key, value);
        } catch (Exception e) {
            throw new KvStorageException(ErrorCode.KVStorageWriteError.getCode(),
                    "Put data failed, key: " + new String(key) + ", detail: " + e.getMessage(), e);
        }
        // after actual storage put success, put it in memory, memory put should success all the time
        super.put(key, value);
    }
    
    @Override
    public void batchPut(List<byte[]> keys, List<byte[]> values) throws KvStorageException {
        if (keys.size() != values.size()) {
            throw new KvStorageException(ErrorCode.KVStorageBatchWriteError,
                    "key's size must be equal to value's size");
        }
        int size = keys.size();
        for (int i = 0; i < size; i++) {
            put(keys.get(i), values.get(i));
        }
    }
    
    @Override
    public void delete(byte[] key) throws KvStorageException {
        try {
            KvStorage storage = createActualStorageIfAbsent(key);
            if (null != storage) {
                storage.delete(key);
            }
        } catch (Exception e) {
            throw new KvStorageException(ErrorCode.KVStorageDeleteError.getCode(),
                    "Delete data failed, key: " + new String(key) + ", detail: " + e.getMessage(), e);
        }
        // after actual storage delete success, put it in memory, memory delete should success all the time
        super.delete(key);
    }
    
    @Override
    public void batchDelete(List<byte[]> keys) throws KvStorageException {
        for (byte[] each : keys) {
            delete(each);
        }
    }
    
    @Override
    public void doSnapshot(String backupPath) throws KvStorageException {
        baseDirStorage.doSnapshot(backupPath);
    }
    
    @Override
    public void snapshotLoad(String path) throws KvStorageException {
        TimerContext.start(LOAD_SNAPSHOT);
        try {
            baseDirStorage.snapshotLoad(path);
            loadSnapshotFromActualStorage(baseDirStorage);
            loadNamespaceSnapshot();
            this.isSnapshotLoad = true;
        } finally {
            TimerContext.end(LOAD_SNAPSHOT, Loggers.CORE);
        }
    }
    
    private void loadSnapshotFromActualStorage(KvStorage actualStorage) throws KvStorageException {
        for (byte[] each : actualStorage.allKeys()) {
            byte[] datum = actualStorage.get(each);
            super.put(each, datum);
        }
    }
    
    private void loadNamespaceSnapshot() {
        for (String each : getAllNamespaceDirs()) {
            try {
                KvStorage kvStorage = createActualStorageIfAbsent(each);
                loadSnapshotFromActualStorage(kvStorage);
            } catch (Exception e) {
                Loggers.CORE.error("load snapshot for namespace {} failed", each, e);
            }
        }
    }
    
    private List<String> getAllNamespaceDirs() {
        File[] files = new File(baseDir).listFiles();
        List<String> result = Collections.emptyList();
        if (null != files) {
            result = new ArrayList<>(files.length);
            for (File each : files) {
                if (each.isDirectory()) {
                    result.add(each.getName());
                }
            }
        }
        return Collections.unmodifiableList(result);
    }
    
    @Override
    public List<byte[]> allKeys() {
        try {
            KvStorage storage = createActualStorageIfAbsent(org.apache.commons.lang3.StringUtils.EMPTY);
            return storage.allKeys();
        } catch (Exception e) {
            throw new DatalinkRuntimeException(DatalinkException.SERVER_ERROR, e);
        }
    }
    
    @Override
    public void shutdown() {
        baseDirStorage.shutdown();
        for (KvStorage each : mqttKvStorage.values()) {
            each.shutdown();
        }
        mqttKvStorage.clear();
        super.shutdown();
    }
    
    private KvStorage createActualStorageIfAbsent(byte[] key) throws Exception {
        String keyString = new String(key);
        return createActualStorageIfAbsent(org.apache.commons.lang3.StringUtils.EMPTY);
    }
    
    private KvStorage createActualStorageIfAbsent(String mqtt) throws Exception {
        if (StringUtils.isBlank(mqtt)) {
            return baseDirStorage;
        }
        
        Function<String, KvStorage> kvStorageBuilder = key -> {
            try {
                String namespacePath = Paths.get(baseDir, key).toString();
                return StorageFactory.createKvStorage(KvStorage.KvType.RocksDB, namespacePath);
            } catch (Exception e) {
                throw new DatalinkRuntimeException(DatalinkException.SERVER_ERROR, e);
            }
        };
        mqttKvStorage.computeIfAbsent(mqtt, kvStorageBuilder);
        return mqttKvStorage.get(mqtt);
    }
}
