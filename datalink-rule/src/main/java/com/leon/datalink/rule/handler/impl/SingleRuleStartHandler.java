package com.leon.datalink.rule.handler.impl;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.RoundRobinPool;
import cn.hutool.core.collection.ListUtil;
import com.leon.datalink.resource.actor.ResourceActor;
import com.leon.datalink.resource.actor.ResourceBroadcastActor;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.rule.handler.AbstractRuleStartHandler;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.actor.TransformActor;

import java.util.LinkedList;
import java.util.List;

/**
 * 单机模式下规则启动处理
 */
public class SingleRuleStartHandler extends AbstractRuleStartHandler {

    @Override
    protected ActorRef createDestResource() {
        return context.actorOf(Props.create(ResourceBroadcastActor.class, rule.getDestResourceList(), ruleActorRef), "dest_broadcast");
    }

    @Override
    protected LinkedList<ActorRef> createTransform(ActorRef destResourceActorRef) {
        LinkedList<ActorRef> transformActorRefList = new LinkedList<>();
        List<Transform> transformList = ListUtil.reverse(rule.getTransformList());
        ActorRef next = destResourceActorRef;
        for (Transform transform : transformList) {
            ActorRef transformActor = context.actorOf(new RoundRobinPool(transform.getWorkerNum()).props(Props.create(TransformActor.class, transform, ruleActorRef, next)), transform.getTransformRuntimeId());
            transformActorRefList.add(transformActor);
            next = transformActor;
        }
        return transformActorRefList;
    }

    @Override
    protected void createSourceResource(LinkedList<ActorRef> transformActorRefList) {
        ActorRef transformActorRef = transformActorRefList.getLast();
        rule.getSourceResourceList().forEach(sourceResource -> context.actorOf((Props.create(ResourceActor.class, sourceResource, DriverModeEnum.SOURCE, ruleActorRef, transformActorRef)),
                sourceResource.getResourceRuntimeId()));
    }

}
