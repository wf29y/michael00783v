package com.leon.datalink.transform.handler.impl;

import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.ScriptUtil;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.handler.TransformHandler;
import org.springframework.util.StringUtils;

import javax.script.*;
import java.util.Map;
import java.util.function.Consumer;

public class ScriptHandler implements TransformHandler {

    private Transform transform;

    private ScriptEngine scriptEngine;

    private String language;

    @Override
    public void init(Transform transform) {
        this.transform = transform;
        this.language = transform.getProperties().getString("language"); // javascript  groovy
        this.scriptEngine = new ScriptEngineManager().getEngineByName(this.language);
    }

    @Override
    public void destroy() {
        scriptEngine = null;
    }

    @Override
    public void transform(RuntimeData runtimeData, Consumer<Object> consumer) {
        String script = transform.getProperties().getString("script");
        if (StringUtils.isEmpty(script) || null == runtimeData) {
            return;
        }
        try {
            // 获取并绑定自定义环境变量
            Bindings bind = scriptEngine.createBindings();

            Map<String, Object> globalVariable = GlobalVariableContent.getAllValue();
            for (String key : globalVariable.keySet()) {
                bind.put(key, globalVariable.get(key));
            }

            scriptEngine.setBindings(bind, ScriptContext.ENGINE_SCOPE);
            scriptEngine.eval(script);
            Invocable jsInvoke = (Invocable) scriptEngine;
            Object scriptResult = jsInvoke.invokeFunction("transform", runtimeData.getData());

            if("javascript".equals(language)){
                scriptResult = ScriptUtil.toJavaObject(scriptResult);
            }

            consumer.accept(scriptResult);
        } catch (Exception e) {
            Loggers.RULE.error("script error {}", e.getMessage());
        }
    }

}
