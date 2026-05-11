package com.leon.datalink.resource;


import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;
import org.beetl.core.statement.PlaceholderST;

import java.util.Map;
import java.util.function.Consumer;

/**
 * 驱动基础类
 */
public abstract class AbstractDriver implements Driver {

    private Consumer<RuntimeData> consumer;

    private TemplateEngine templateEngine;

    static {
        // 模板引擎输出json
        PlaceholderST.output = (ctx, value) -> {
            if (value instanceof String) {
                ctx.byteWriter.writeString(value.toString());
            } else {
                ctx.byteWriter.writeString(JacksonUtils.toJson(value));
            }
        };
    }

    @Override
    public final void init(Consumer<RuntimeData> consumer) {
        this.consumer = consumer;
        this.templateEngine = TemplateUtil.createEngine();
    }

    // 获得全局变量
    protected final Map<String, Object> getVariable(Object data) {
        Map<String, Object> globalVar = GlobalVariableContent.getAllValue();
        if (JacksonUtils.canToMap(data)) {
            try {
                globalVar.putAll(JacksonUtils.convertValue(data, new TypeReference<Map<String, Object>>() {
                }));
            } catch (Exception e) {
                Loggers.DRIVER.error("data to map error {}", e.getMessage());
            }
        }
        return globalVar;
    }

    protected final String templateAnalysis(String template, Map<?, ?> data) {
        return this.templateEngine.getTemplate(template).render(data);
    }

    // 产出数据
    protected final void produceData(Object data) {
        consumer.accept(new RuntimeData(RuntimeTypeEnum.SOURCE).success(data));
    }

    // 产出数据错误
    protected final void produceDataError(String errorMessage) {
        consumer.accept(new RuntimeData(RuntimeTypeEnum.SOURCE).fail(errorMessage));
    }

}
