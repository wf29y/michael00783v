

package com.leon.datalink.core.storage;

import com.leon.datalink.core.storage.kv.MemoryKvStorage;
import com.leon.datalink.core.storage.kv.RocksDBStorage;

/**
 * Ket-value Storage factory.
 *
 * @author Leon
 */
public final class StorageFactory {
    
    /**
     * Create {@link KvStorage} implementation.
     *
     * @param type    type of {@link KvStorage}
     * @param baseDir base dir of storage file.
     * @return implementation of {@link KvStorage}
     * @throws Exception exception during creating {@link KvStorage}
     */
    public static KvStorage createKvStorage(KvStorage.KvType type, final String baseDir)
            throws Exception {
        switch (type) {
            case Memory:
                return new MemoryKvStorage();
            case RocksDB:
                return new RocksDBStorage(baseDir);
            default:
                throw new IllegalArgumentException("this kv type : [" + type.name() + "] not support");
        }
    }
    
}
