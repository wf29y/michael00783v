package com.leon.datalink.web.service.impl;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.entity.Runtime;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.web.service.RuleService;
import com.leon.datalink.web.service.RuntimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.leon.datalink.core.common.Constants.STORAGE_PATH;

@Service
public class RuntimeServiceImpl implements RuntimeService {

    @Autowired
    @Lazy
    RuleService ruleService;

    /**
     * key value storage
     */
    private final KvStorage kvStorage;

    /**
     * 运行状态持久化路径
     */
    private final static String RUNTIME_PATH = "/runtime";


    public RuntimeServiceImpl() throws Exception {

        // 持久化读入RuntimeManger
        // init storage
        this.kvStorage = new DatalinkKvStorage(STORAGE_PATH + RUNTIME_PATH);

        // read resource list form storage
        if (this.kvStorage.allKeys().size() <= 0) return;
        for (byte[] key : this.kvStorage.allKeys()) {
            String ruleId = new String(key);
            byte[] value = this.kvStorage.get(key);
            Runtime runtime = JacksonUtils.toObj(value, Runtime.class);
            RuntimeManger.setRuntime(ruleId, runtime);
        }

    }

    @PreDestroy
    public void destroy() throws KvStorageException {
        // 系统停止时写入持久化
        ConcurrentHashMap<String, Runtime> runtimeList = RuntimeManger.getRuntimeList();
        Enumeration<String> keys = runtimeList.keys();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            this.kvStorage.put(key.getBytes(), JacksonUtils.toJsonBytes(runtimeList.get(key)));
        }
        Loggers.RUNTIME.info("runtime persistence");
    }


    @Override
    public Runtime getRuntime(String ruleId) {
        return RuntimeManger.getRuntime(ruleId);
    }


    @Override
    public void remove(String ruleId) throws KvStorageException {
        RuntimeManger.removeRuntime(ruleId);
        this.kvStorage.delete(ruleId.getBytes());
    }


    @Override
    public void resetRuntime(String ruleId) throws KvStorageException {
        Rule rule = ruleService.get(ruleId);
        List<String> sourceRuntimeIdList = rule.getSourceResourceList().stream().map(Resource::getResourceRuntimeId).collect(Collectors.toList());
        List<String> destRuntimeIdList = rule.getDestResourceList().stream().map(Resource::getResourceRuntimeId).collect(Collectors.toList());
        List<String> transformRuntimeIdList = rule.getTransformList().stream().map(Transform::getTransformRuntimeId).collect(Collectors.toList());
        RuntimeManger.resetRuntime(rule.getRuleId(), sourceRuntimeIdList, destRuntimeIdList, transformRuntimeIdList);
        this.kvStorage.delete(ruleId.getBytes());
    }


    @Override
    public void initRuntime(Rule rule) throws KvStorageException {
        List<String> sourceRuntimeIdList = rule.getSourceResourceList().stream().map(Resource::getResourceRuntimeId).collect(Collectors.toList());
        List<String> destRuntimeIdList = rule.getDestResourceList().stream().map(Resource::getResourceRuntimeId).collect(Collectors.toList());
        List<String> transformRuntimeIdList = rule.getTransformList().stream().map(Transform::getTransformRuntimeId).collect(Collectors.toList());
        RuntimeManger.init(rule.getRuleId(), sourceRuntimeIdList, destRuntimeIdList, transformRuntimeIdList);
    }
}
