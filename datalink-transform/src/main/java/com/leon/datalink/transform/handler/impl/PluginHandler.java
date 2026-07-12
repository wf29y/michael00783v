package com.leon.datalink.transform.handler.impl;

import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.EnvUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.plugin.DataLinkTransformPlugin;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import com.leon.datalink.transform.plugin.Plugin;
import com.leon.datalink.transform.plugin.PluginFactory;

import java.nio.file.Paths;
import java.util.function.Consumer;

public class PluginHandler implements TransformHandler {

    private DataLinkTransformPlugin transformPlugin;

    @Override
    public void init(Transform transform) {
        ConfigProperties properties = transform.getProperties();
        // 创建插件
        Plugin plugin = properties.getObject("plugin", Plugin.class);
        try {
            this.transformPlugin = PluginFactory.createTransformPlugin(Paths.get(EnvUtil.getPluginFilePath(), plugin.getPluginName()).toString(), plugin.getPackagePath());
            if (null != transformPlugin) {
                transformPlugin.create();
            }
        } catch (Exception e) {
            Loggers.RULE.error("create plugin error {}", e.getMessage());
        }
    }

    @Override
    public void destroy() {
        if (null != transformPlugin) {
            transformPlugin.destroy();
        }
    }

    @Override
    public void transform(RuntimeData runtimeData, Consumer<Object> consumer) {
        // 环境变量
        transformPlugin.setVariable(GlobalVariableContent.getAllValue());
        Object transform = transformPlugin.transform(JacksonUtils.toJson(runtimeData.getData()));
        consumer.accept(transform);
    }
}
