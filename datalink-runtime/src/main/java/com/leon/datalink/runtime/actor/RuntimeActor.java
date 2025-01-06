package com.leon.datalink.runtime.actor;

import akka.actor.AbstractActor;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.runtime.RuntimeManger;

import java.util.Map;


public class RuntimeActor extends AbstractActor {

    private final String ruleId;

    private final Map<String, Object> variables;

    public RuntimeActor(String ruleId, Map<String, Object> variables) {
        this.ruleId = ruleId;
        this.variables = variables;
    }

    @Override
    public void preStart() {
        Loggers.RUNTIME.info("start runtime [{}]", getSelf().path());
        RuntimeManger.init(ruleId, variables);
    }

    @Override
    public void postStop() {
        Loggers.RUNTIME.info("stop  runtime [{}]", getSelf().path());
       // RuntimeManger.removeRuntime(ruleId); 停止时不删除运行状态
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RuntimeUpdateDataMsg.class, msg -> RuntimeManger.updateData(ruleId, msg))
                //.match(RuntimeUpdateVarMsg.class, msg -> RuntimeManger.updateVariable(ruleId, msg))
                .build();
    }
}
