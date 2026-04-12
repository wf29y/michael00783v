package com.leon.datalink.resource;


import akka.actor.ActorRef;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;
import org.beetl.core.statement.PlaceholderST;

import java.util.Map;

/**
 * 驱动基础类
 */
public abstract class AbstractDriver implements Driver {

    private ActorRef driverActorRef;

    private TemplateEngine templateEngine;

    private String ruleId;

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
    public final void init(ActorRef driverActorRef, String ruleId) {
        this.driverActorRef = driverActorRef;
        this.ruleId = ruleId;
        this.templateEngine = TemplateUtil.createEngine();
    }

    // 获得全局变量
    protected final Map<String, Object> getVariable(Object data) {
        Map<String, Object> globalVar = GlobalVariableContent.getAllValue();
        Map<String, Object> ruleVar = RuntimeManger.getVariables(ruleId);
        if (null != ruleVar) {
            globalVar.putAll(ruleVar);
        }
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
        RuntimeData runtimeData = new RuntimeData(RuntimeTypeEnum.SOURCE).success(data);
        driverActorRef.tell(runtimeData, ActorRef.noSender());
    }

    // 产出数据错误
    protected final void produceDataError(String errorMessage) {
        RuntimeData runtimeData = new RuntimeData(RuntimeTypeEnum.SOURCE).fail(errorMessage);
        driverActorRef.tell(runtimeData, ActorRef.noSender());
    }

}
