package com.leon.datalink.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.storage.DatalinkKvStorage;
import com.leon.datalink.core.storage.KvStorage;
import com.leon.datalink.core.storage.kv.FileKvStorage;
import com.leon.datalink.core.utils.EnvUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.transform.plugin.Plugin;
import com.leon.datalink.web.service.PluginService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.kvStorage = new DatalinkKvStorage(EnvUtil.getStoragePath() + PLUGIN_PATH);

        // init file storage
        this.fileStorage = new FileKvStorage(EnvUtil.getPluginFilePath());

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
    public void upload(String pluginName, byte[] file) throws KvStorageException {
        fileStorage.put(pluginName.getBytes(), file);
    }

    @Override
    public byte[] download(String fileName) throws KvStorageException {
        return this.fileStorage.get(fileName.getBytes());
    }

    @Override
    public Plugin get(String pluginId) {
        return pluginList.get(pluginId);
    }

    @Override
    public void add(Plugin plugin) throws KvStorageException {
        if (StringUtils.isEmpty(plugin.getPluginId())) plugin.setPluginId(SnowflakeIdWorker.getId());
        this.kvStorage.put(plugin.getPluginId().getBytes(), JacksonUtils.toJsonBytes(plugin));
        pluginList.put(plugin.getPluginId(), plugin);
    }

    @Override
    public void remove(String pluginId) throws KvStorageException {
        Plugin plugin = this.get(pluginId);
        this.kvStorage.delete(pluginId.getBytes());
        this.fileStorage.delete(plugin.getPluginName().getBytes());
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
        return CollectionUtil.reverse(stream.sorted(Comparator.comparingLong(item -> Long.parseLong(item.getPluginId()))).collect(Collectors.toList()));
    }

    @Override
    public int getCount(Plugin plugin) {
        return this.list(plugin).size();
    }


}
