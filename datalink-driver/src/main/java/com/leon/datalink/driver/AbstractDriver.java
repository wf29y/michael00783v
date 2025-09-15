package com.leon.datalink.driver;


import akka.actor.ActorRef;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.driver.actor.ReceiveDataMsg;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.runtime.RuntimeManger;

import java.util.List;
import java.util.Map;

/**
 * 驱动基础类
 */
public abstract class AbstractDriver implements Driver {

    private ActorRef ruleActorRef;

    private TemplateEngine templateEngine;

    private String ruleId;

    @Override
    public final void init(ActorRef ruleActorRef, String ruleId) {
        this.ruleActorRef = ruleActorRef;
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

    // 发送数据
    protected final void sendData(Object data) {
        ruleActorRef.tell(new ReceiveDataMsg(data), ActorRef.noSender());
    }

}
