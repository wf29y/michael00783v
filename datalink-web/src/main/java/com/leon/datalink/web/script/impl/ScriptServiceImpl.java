package com.leon.datalink.web.script.impl;

import akka.actor.ActorSystem;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.rule.script.Script;
import com.leon.datalink.web.script.ScriptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
public class ScriptServiceImpl implements ScriptService {
    /**
     * actor syatem
     */
    ActorSystem actorSystem;

    /**
     * 资源列表
     */
    private final ConcurrentHashMap<String, Script> scriptList = new ConcurrentHashMap<>();

    /**
     * key value storage
     */
    private final KvStorage kvStorage;

    /**
     * 脚本持久化路径
     */
    private final static String SCRIPT_PATH = "/script";

    public ScriptServiceImpl(ActorSystem actorSystem) throws Exception {

        // actor system
        this.actorSystem = actorSystem;

        // init storage
        this.kvStorage = new DatalinkKvStorage(STORAGE_PATH + SCRIPT_PATH);

        // read script list form storage
        if (this.kvStorage.allKeys().size() <= 0) return;
        for (byte[] key : this.kvStorage.allKeys()) {
            String scriptId = new String(key);
            byte[] value = this.kvStorage.get(key);
            Script script = JacksonUtils.toObj(value, Script.class);
            scriptList.put(scriptId, script);
        }

    }

    @Override
    public Script get(String scriptId) {
        return scriptList.get(scriptId);
    }

    @Override
    public void add(Script script) throws KvStorageException {
        if (StringUtils.isEmpty(script.getScriptId())) script.setScriptId(SnowflakeIdWorker.getId());
        this.kvStorage.put(script.getScriptId().getBytes(), JacksonUtils.toJsonBytes(script));
        scriptList.put(script.getScriptId(), script);
    }

    @Override
    public void remove(Script script) throws KvStorageException {
        this.kvStorage.delete(script.getScriptId().getBytes());
        scriptList.remove(script.getScriptId());
    }

    @Override
    public void update(Script script) throws KvStorageException {
        this.kvStorage.put(script.getScriptId().getBytes(), JacksonUtils.toJsonBytes(script));
        scriptList.put(script.getScriptId(), script);
    }

    @Override
    public List<Script> list(Script script) {
        Stream<Script> stream = scriptList.values().stream();
        if (null != script) {
            if (!StringUtils.isEmpty(script.getScriptId())) {
                stream = stream.filter(s -> s.getScriptId().equals(script.getScriptId()));
            }
            if (!StringUtils.isEmpty(script.getScriptName())) {
                stream = stream.filter(s -> s.getScriptName().contains(script.getScriptName()));
            }
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    public int getCount(Script script) {
        return this.list(script).size();
    }


}
