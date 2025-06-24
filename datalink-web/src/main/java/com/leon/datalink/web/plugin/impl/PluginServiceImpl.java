package com.leon.datalink.web.plugin.impl;

import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.storage.kv.FileKvStorage;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.rule.entity.Plugin;
import com.leon.datalink.rule.entity.Script;
import com.leon.datalink.web.plugin.PluginService;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
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
public class PluginServiceImpl implements PluginService {

    /**
     * 插件列表
     */
    private final ConcurrentHashMap<String, Plugin> pluginList = new ConcurrentHashMap<>();

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
    private final static String PLUGIN_PATH = "/plugin";

    public PluginServiceImpl() throws Exception {

        // init storage
        this.kvStorage = new DatalinkKvStorage(STORAGE_PATH + PLUGIN_PATH);

        // init file storage
        this.fileStorage = new FileKvStorage(Constants.PLUGIN_FILE_PATH);

        // read plugin list form storage
        if (this.kvStorage.allKeys().size() <= 0) return;
        for (byte[] key : this.kvStorage.allKeys()) {
            String pluginId = new String(key);
            byte[] value = this.kvStorage.get(key);
            Plugin plugin = JacksonUtils.toObj(value, Plugin.class);
            pluginList.put(pluginId, plugin);
        }

    }

    @Override
    public void upload(String fileName, byte[] file) throws KvStorageException {
         fileStorage.put(fileName.getBytes(),file);
    }

    @Override
    public Plugin get(String scriptId) {
        return pluginList.get(scriptId);
    }

    @Override
    public void add(Plugin plugin) throws KvStorageException {
        if (StringUtils.isEmpty(plugin.getPluginId())) plugin.setPluginId(SnowflakeIdWorker.getId());
        this.kvStorage.put(plugin.getPluginId().getBytes(), JacksonUtils.toJsonBytes(plugin));
        pluginList.put(plugin.getPluginId(), plugin);
    }

    @Override
    public void remove(Plugin plugin) throws KvStorageException {
        this.kvStorage.delete(plugin.getPluginId().getBytes());
        pluginList.remove(plugin.getPluginId());
    }

    @Override
    public void update(Plugin plugin) throws KvStorageException {
        this.kvStorage.put(plugin.getPluginId().getBytes(), JacksonUtils.toJsonBytes(plugin));
        pluginList.put(plugin.getPluginId(), plugin);
    }

    @Override
    public List<Plugin> list(Plugin plugin) {
        Stream<Plugin> stream = pluginList.values().stream();
        if (null != plugin) {
            if (!StringUtils.isEmpty(plugin.getPluginName())) {
                stream = stream.filter(s -> s.getPluginName().contains(plugin.getPluginName()));
            }
        }
        return stream.collect(Collectors.toList());
    }

    @Override
    public int getCount(Plugin plugin) {
        return this.list(plugin).size();
    }



}
