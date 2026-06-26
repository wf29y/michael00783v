package com.leon.datalink.rule.actor;

import akka.actor.AbstractActor;
import com.leon.datalink.core.utils.EnvUtil;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.handler.impl.ClusterRuleStartHandler;
import com.leon.datalink.rule.handler.impl.SingleRuleStartHandler;
import com.leon.datalink.runtime.RuntimeManger;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.runtime.entity.RuntimeStatus;

public class RuleActor extends AbstractActor {

    private final Rule rule;

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
            new ClusterRuleStartHandler().start(rule, getContext());
        } else {
            new SingleRuleStartHandler().start(rule, getContext());
        }
    }

    @Override
    public void postStop() throws Exception {
        Loggers.RULE.info("stop rule [{}]", getSelf().path());
        RuntimeManger.stopRuntime(rule.getRuleId());
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
