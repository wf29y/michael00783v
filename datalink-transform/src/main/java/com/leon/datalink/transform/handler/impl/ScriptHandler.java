package com.leon.datalink.transform.handler.impl;

import cn.hutool.core.map.MapUtil;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.ScriptUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import org.springframework.util.StringUtils;

import javax.script.*;
import java.util.Map;

public class ScriptHandler implements TransformHandler {

    private Transform transform;

    private ScriptEngine scriptEngine;

    @Override
    public void init(Transform transform) {
        this.transform = transform;
        this.scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
    }

    @Override
    public void destroy() {
        scriptEngine = null;
    }

    @Override
    public Object transform(Object data) {
        String script = transform.getProperties().getString("script");
        if (StringUtils.isEmpty(script) || null == data) {
            return null;
        }
        try {
            String ruleId = transform.getRuleId();

            // 获取并绑定自定义环境变量
            Bindings bind = scriptEngine.createBindings();

            Map<String, Object> globalVariable = GlobalVariableContent.getAllValue();
            for (String key : globalVariable.keySet()) {
                bind.put(key, globalVariable.get(key));
            }

            Map<String, Object> variables = RuntimeManger.getVariables(ruleId);
            for (String key : variables.keySet()) {
                bind.put(key, variables.get(key));
            }

            scriptEngine.setBindings(bind, ScriptContext.ENGINE_SCOPE);
            scriptEngine.eval(script);
            Invocable jsInvoke = (Invocable) scriptEngine;
            Object scriptResult = jsInvoke.invokeFunction("transform", data);

            // 更新自定义环境变量
            if (MapUtil.isNotEmpty(variables)) {
                variables.replaceAll((k, v) -> scriptEngine.getContext().getAttribute(k));
            }

            return ScriptUtil.toJavaObject(scriptResult);
        } catch (Exception e) {
            Loggers.RULE.error("script error {}", e.getMessage());
        }
        return null;
    }

}
