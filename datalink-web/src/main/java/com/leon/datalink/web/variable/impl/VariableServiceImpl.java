package com.leon.datalink.web.variable.impl;

import com.google.common.collect.Lists;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.core.variable.Variable;
import com.leon.datalink.core.variable.VariableTypeEnum;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.web.backup.BackupData;
import com.leon.datalink.web.variable.VariableService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.leon.datalink.core.common.Constants.STORAGE_PATH;

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

        // 持久化读入GlobalVariableContent
        // init storage
        this.kvStorage = new DatalinkKvStorage(STORAGE_PATH + VARIABLE_PATH);

        // read resource list form storage
        if (this.kvStorage.allKeys().size() <= 0) return;
        for (byte[] key : this.kvStorage.allKeys()) {
            String varKey = new String(key);
            byte[] value = this.kvStorage.get(key);
            Variable variable = JacksonUtils.toObj(value, Variable.class);
            GlobalVariableContent.set(varKey, variable);
        }

    }

    @Override
    public Variable get(String id) {
        return GlobalVariableContent.get(id);
    }

    @Override
    public void add(Variable variable) throws KvStorageException {
//        Variable var = GlobalVariableContent.get(variable.getKey());
//        if (null != var) return;
        variable.setType(VariableTypeEnum.CUSTOM);
        GlobalVariableContent.set(variable.getKey(), variable);
        this.kvStorage.put(variable.getKey().getBytes(), JacksonUtils.toJsonBytes(variable));
    }

    @Override
    public void update(Variable variable) throws KvStorageException {
        if (!VariableTypeEnum.CUSTOM.equals(variable.getType())) return;
        GlobalVariableContent.set(variable.getKey(), variable);
        this.kvStorage.put(variable.getKey().getBytes(), JacksonUtils.toJsonBytes(variable));
    }

    @Override
    public void remove(String key) throws KvStorageException {
        Variable variable = this.get(key);
        if (!VariableTypeEnum.CUSTOM.equals(variable.getType())) return;
        GlobalVariableContent.remove(key);
        this.kvStorage.delete(key.getBytes());
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
        return stream.sorted(Comparator.comparing(Variable::getType)).collect(Collectors.toList());
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
