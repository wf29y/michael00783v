package com.leon.datalink.web.runtime;


import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.runtime.entity.Runtime;


public interface RuntimeService {

   Runtime getRuntime(String ruleId);

   void remove(String ruleId) throws KvStorageException;

    void resetRuntime(String ruleId) throws KvStorageException;

}
