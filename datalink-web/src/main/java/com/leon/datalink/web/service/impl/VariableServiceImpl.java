package com.leon.datalink.web.service.impl;

import com.google.common.collect.Lists;
import com.leon.datalink.cluster.distributed.ConsistencyManager;
import com.leon.datalink.cluster.distributed.ConsistencyWrapper;
import com.leon.datalink.core.backup.BackupData;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.core.variable.Variable;
import com.leon.datalink.core.variable.VariableTypeEnum;
import com.leon.datalink.web.service.VariableService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VariableServiceImpl implements VariableService, BackupData<Variable> {

    /**
     * key value storage
     */
    private final KvStorage kvStorage;

    /**
     * 变量持久化路径
     */
    private final static String VARIABLE_PATH = "/variable";


    public VariableServiceImpl() throws Exception {

        // init storage
        this.kvStorage = new DatalinkKvStorage(EnvUtil.getStoragePath() + VARIABLE_PATH);

        Set<Variable> variables = new HashSet<>();
        for (byte[] key : this.kvStorage.allKeys()) {
            byte[] value = this.kvStorage.get(key);
            Variable variable = JacksonUtils.toObj(value, Variable.class);
            variables.add(variable);
        }
        GlobalVariableContent.setAllCustomVariables(variables);
    }

    @Override
    public Variable get(String id) {
        return GlobalVariableContent.get(id);
    }

    @Override
    public void add(Variable variable) throws KvStorageException {
        variable.setType(VariableTypeEnum.CUSTOM);
        GlobalVariableContent.set(variable.getKey(), variable);
        this.kvStorage.put(variable.getKey().getBytes(), JacksonUtils.toJsonBytes(variable));
        ConsistencyManager.sync(ConsistencyWrapper.add(Variable.class, variable));
    }

    @Override
    public void update(Variable variable) throws KvStorageException {
        if (!VariableTypeEnum.CUSTOM.equals(variable.getType())) return;
        GlobalVariableContent.set(variable.getKey(), variable);
        this.kvStorage.put(variable.getKey().getBytes(), JacksonUtils.toJsonBytes(variable));
        ConsistencyManager.sync(ConsistencyWrapper.add(Variable.class, variable));
    }

    @Override
    public void remove(String key) throws KvStorageException {
        Variable variable = this.get(key);
        if (!VariableTypeEnum.CUSTOM.equals(variable.getType())) return;
        GlobalVariableContent.remove(key);
        this.kvStorage.delete(key.getBytes());
        ConsistencyManager.sync(ConsistencyWrapper.delete(Variable.class, variable));
    }

    @Override
    public List<Variable> list(Variable variable) {
        Map<String, Variable> all = GlobalVariableContent.getAll();
        Stream<Variable> stream = Lists.newArrayList(all.values()).stream();
        if (null != variable) {
            if (!StringUtils.isEmpty(variable.getKey())) {
                stream = stream.filter(r -> r.getKey().contains(variable.getKey()));
            }
            if (null != variable.getType()) {
                stream = stream.filter(r -> r.getType().equals(variable.getType()));
            }
        }
        return stream.sorted(Comparator.comparing(Variable::getType).thenComparing(Variable::getKey)).collect(Collectors.toList());
    }

    @Override
    public int getCount(Variable variable) {
        return GlobalVariableContent.getAll().size();
    }

    @Override
    public String dataKey() {
        return "variables";
    }

    @Override
    public List<Variable> createBackup() {
        Variable variable = new Variable();
        variable.setType(VariableTypeEnum.CUSTOM);
        return this.list(variable);
    }

    @Override
    public void recoverBackup(List<Variable> dataList) {
        try {
            Variable variable = new Variable();
            variable.setType(VariableTypeEnum.CUSTOM);
            List<Variable> list = this.list(variable);
            for (Variable var : list) {
                this.remove(var.getKey());
            }
            for (Variable var : dataList) {
                this.add(var);
            }
        } catch (KvStorageException e) {
            Loggers.WEB.error("recover variable backup error {}", e.getMessage());
        }
    }
}
