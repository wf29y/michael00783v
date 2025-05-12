package com.leon.datalink.rule.transform.impl;

import cn.hutool.core.map.MapUtil;
import com.leon.datalink.core.common.Constants;
import com.leon.datalink.core.plugin.PluginFactory;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.plugin.DataLinkTransformPlugin;
import com.leon.datalink.rule.entity.Plugin;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.transform.TransformHandler;
import com.leon.datalink.runtime.RuntimeManger;

import java.util.Map;

public class PluginHandler implements TransformHandler {

    private Rule rule;

    private DataLinkTransformPlugin transformPlugin;

    @Override
    public void init(Rule rule) {
        this.rule = rule;
        // 创建插件
        Plugin plugin = rule.getPlugin();
        try {
            this.transformPlugin = PluginFactory.createTransformPlugin(Constants.PLUGIN_FILE_PATH + plugin.getPluginName(), plugin.getPackagePath());
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
    public Object transform(Object data) {
        // 环境变量
        Map<String, Object> globalProp = GlobalVariableContent.getAllValue();
        Map<String, Object> ruleProp = RuntimeManger.getVariables(rule.getRuleId());
        if (null != ruleProp) {
            globalProp.putAll(ruleProp);
        }
        transformPlugin.setVariable(globalProp);

        Object transform = transformPlugin.transform(JacksonUtils.toJson(data));

        // 更新自定义环境变量
        Map<String, Object> variable = transformPlugin.getVariable();
        if (null != ruleProp && MapUtil.isNotEmpty(variable)) {
            ruleProp.replaceAll((k, v) -> variable.get(k));
        }

        return transform;
    }
}
