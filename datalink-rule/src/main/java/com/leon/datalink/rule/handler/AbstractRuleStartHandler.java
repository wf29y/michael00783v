package com.leon.datalink.rule.handler;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import com.leon.datalink.rule.entity.Rule;
import scala.collection.immutable.Seq;

import java.util.LinkedList;

public abstract class AbstractRuleStartHandler implements RuleStartHandler {

    protected Rule rule;

    protected ActorContext context;

    protected ActorRef ruleActorRef;

    @Override
    public void start(Rule rule, ActorContext context) {
        this.rule = rule;
        this.context = context;
        this.ruleActorRef = context.self();

        // 创建目的资源
        ActorRef destResourceActorRef = createDestResource();

        // 创建转换
        LinkedList<ActorRef> transformActorRefList = createTransform(destResourceActorRef);

        // 创建数据源
        createSourceResource(transformActorRefList);

    }

    protected abstract ActorRef createDestResource();

    protected abstract LinkedList<ActorRef> createTransform(ActorRef destResourceActorRef);

    protected abstract void createSourceResource(LinkedList<ActorRef> transformActorRefList);

}
