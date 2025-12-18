package com.leon.datalink.transform.handler.impl;

import cn.hutool.core.map.MapUtil;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.plugin.DataLinkTransformPlugin;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import com.leon.datalink.transform.plugin.Plugin;
import com.leon.datalink.transform.plugin.PluginFactory;

import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Consumer;

public class PluginHandler implements TransformHandler {

    private Transform transform;

    private DataLinkTransformPlugin transformPlugin;

    @Override
    public void init(Transform transform) {
        this.transform = transform;
        ConfigProperties properties = transform.getProperties();
        // 创建插件
        Plugin plugin = properties.getObject("plugin", Plugin.class);
        try {
            this.transformPlugin = PluginFactory.createTransformPlugin(Paths.get(Constants.PLUGIN_FILE_PATH, plugin.getPluginName()).toString(), plugin.getPackagePath());
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
        Map<String, Object> globalProp = GlobalVariableContent.getAllValue();
        Map<String, Object> ruleProp = RuntimeManger.getVariables(transform.getRuleId());
        if (null != ruleProp) {
            globalProp.putAll(ruleProp);
        }
        transformPlugin.setVariable(globalProp);

        Object transform = transformPlugin.transform(JacksonUtils.toJson(runtimeData.getData()));

        // 更新自定义环境变量
        Map<String, Object> variable = transformPlugin.getVariable();
        if (null != ruleProp && MapUtil.isNotEmpty(variable)) {
            ruleProp.replaceAll((k, v) -> variable.get(k));
        }

        consumer.accept(transform);
    }
}
