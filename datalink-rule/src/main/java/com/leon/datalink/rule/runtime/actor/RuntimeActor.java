package com.leon.datalink.rule.runtime.actor;

import akka.actor.AbstractActor;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.runtime.RuntimeService;

public class RuntimeActor extends AbstractActor {

    private final Rule rule;

    private final RuntimeService runtimeService;

    public RuntimeActor(Rule rule, RuntimeService runtimeService) {
        this.rule = rule;
        this.runtimeService = runtimeService;
    }

    @Override
    public void preStart() {
        Loggers.DRIVER.info("runtime actor start");
        runtimeService.init(rule);
    }

    @Override
    public void postStop() {
        Loggers.DRIVER.info("runtime actor stop");
        runtimeService.remove(rule.getRuleId());
    }

    @Override
    public Receive createReceive() {
        //调用runtimeService存储
        return receiveBuilder().match(RuntimeUpdateMsg.class, msg -> runtimeService.update(rule.getRuleId(), msg)).build();
    }
}
