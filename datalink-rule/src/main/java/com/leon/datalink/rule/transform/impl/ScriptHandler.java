package com.leon.datalink.rule.transform.impl;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.transform.TransformHandler;
import com.leon.datalink.runtime.RuntimeManger;
import org.springframework.util.StringUtils;

import javax.script.*;
import java.util.Map;

public class ScriptHandler implements TransformHandler {

    private Rule rule;

    private ScriptEngine scriptEngine;

    @Override
    public void init(Rule rule) {
        this.rule = rule;
        this.scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
    }

    @Override
    public void destroy() {
        scriptEngine = null;
    }

    @Override
    public Object transform(Object data) {
        String script = rule.getScript();
        if (StringUtils.isEmpty(script) || null == data) {
            return null;
        }
        try {
            String ruleId = rule.getRuleId();

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
            Object transform = jsInvoke.invokeFunction("transform", data);

            // 更新自定义环境变量
            if (MapUtil.isNotEmpty(variables)) {
                variables.replaceAll((k, v) -> scriptEngine.getContext().getAttribute(k));
            }
            return new ObjectMapper().convertValue(transform, Object.class);
        } catch (Exception e) {
            Loggers.RULE.error("script error {}", e.getMessage());
        }
        return null;
    }
}
