package com.leon.datalink.rule.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.leon.datalink.core.utils.EnvUtil;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.handler.RuleCreateHandler;
import com.leon.datalink.rule.handler.impl.ClusterRuleCreateHandler;
import com.leon.datalink.rule.handler.impl.SingleRuleCreateHandler;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.runtime.entity.RuntimeStatus;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private RuleCreateHandler ruleCreateHandler;

    public static Props props(Rule rule) {
        return Props.create(RuleActor.class, () -> new RuleActor(rule));
    }

    public RuleActor(Rule rule) {
        this.rule = rule;
    }

    /**
     * 启动rule
     */
    @Override
    public void preStart() {
        Loggers.RULE.info("start rule [{}]", getSelf().path());
        if (EnvUtil.isCluster()) {
            ruleCreateHandler = new ClusterRuleCreateHandler();
        } else {
            ruleCreateHandler = new SingleRuleCreateHandler();
        }
        ruleCreateHandler.create(rule, getContext());
    }

    @Override
    public void postStop() throws Exception {
        Loggers.RULE.info("stop rule [{}]", getSelf().path());
        RuntimeManger.stopRuntime(rule.getRuleId());
        ruleCreateHandler.destroy();
    }

    /**
     * 处理runtime
     *
     * @return
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(RuntimeData.class, runtimeData -> RuntimeManger.handleRecord(rule.getRuleId(), runtimeData))
                .match(RuntimeStatus.class, runtimeStatus -> RuntimeManger.handleStatus(rule.getRuleId(), runtimeStatus))
                .build();
    }


}
