package com.leon.datalink.web.plugin;

import com.leon.datalink.core.exception.KvStorageException;
import com.leon.datalink.core.service.BaseService;
import com.leon.datalink.rule.entity.Plugin;
import com.leon.datalink.rule.entity.Script;

public interface PluginService extends BaseService<Plugin> {

    void upload(String pluginName,byte[] file) throws KvStorageException;

    byte[] download(String pluginName) throws KvStorageException;

}
