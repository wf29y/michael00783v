package com.leon.datalink.rule.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import cn.hutool.core.collection.ListUtil;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.actor.DriverActor;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.actor.TransformMasterActor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class RuleActor extends AbstractActor {

    private final Rule rule;

    private List<ActorRef> sourceActorRefList;

    private List<ActorRef> destActorRefList;

    private final LinkedList<ActorRef> transformActorRefList = new LinkedList<>();

    public RuleActor(Rule rule) {
        this.rule = rule;
    }

    @Override
    public void preStart() {
        ActorContext context = getContext();

        // 创建数据转换actor
        List<Transform> transformList = ListUtil.reverse(rule.getTransformList());
        ActorRef next = getSelf();
        for (Transform transform : transformList) {
            ActorRef transformActor = context.actorOf(Props.create(TransformMasterActor.class, transform, next), transform.getTransformRuntimeId());
            transformActorRefList.add(transformActor);
            next = transformActor;
        }

        // 创建目的actor
        destActorRefList = rule.getDestResourceList().stream().map(destResource -> context.actorOf((Props.create(DriverActor.class, destResource.getResourceType().getDriver(), destResource.getProperties(), DriverModeEnum.DEST, rule.getRuleId(), destResource.getResourceRuntimeId())),
                destResource.getResourceRuntimeId())).collect(Collectors.toList());

        // 创建源actor
        sourceActorRefList = rule.getSourceResourceList().stream().map(sourceResource -> context.actorOf((Props.create(DriverActor.class, sourceResource.getResourceType().getDriver(), sourceResource.getProperties(), DriverModeEnum.SOURCE, rule.getRuleId(), sourceResource.getResourceRuntimeId())),
                sourceResource.getResourceRuntimeId())).collect(Collectors.toList());

        Loggers.RULE.info("start rule [{}]", getSelf().path());
    }

    @Override
    public void postStop() {
        sourceActorRefList.clear();
        destActorRefList.clear();
        transformActorRefList.clear();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuntimeData.class, dataRecord -> {
            switch (dataRecord.getType()) {
                // 来自数据源actor的数据 转发给第一个转换actor
                case SOURCE: {
                    transformActorRefList.getLast().tell(dataRecord, getSelf());
                    break;
                }
                // 来自转换actor的数据 转发给所有目的地actor
                case TRANSFORM: {
                    for (ActorRef actorRef : destActorRefList) {
                        actorRef.tell(dataRecord, getSelf());
                    }
                    break;
                }
            }
        }).build();
    }


}
