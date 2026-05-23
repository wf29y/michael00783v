package com.leon.datalink.web.service;


import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.runtime.entity.Runtime;


public interface RuntimeService {

    Runtime getRuntime(String ruleId);

    void remove(String ruleId) throws KvStorageException;

    void resetRuntime(String ruleId) throws KvStorageException;

    void initRuntime(Rule rule) throws KvStorageException;

}
