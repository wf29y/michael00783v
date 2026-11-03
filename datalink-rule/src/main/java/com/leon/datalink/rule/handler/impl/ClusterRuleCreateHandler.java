package com.leon.datalink.rule.handler.impl;

import akka.actor.ActorRef;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.routing.RoundRobinPool;
import cn.hutool.core.collection.ListUtil;
import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.resource.actor.ResourceActor;
import com.leon.datalink.resource.actor.ResourceBroadcastActor;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.resource.constans.SourceModeEnum;
import com.leon.datalink.rule.handler.AbstractRuleCreateHandler;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.actor.TransformActor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * 集群模式下规则启动处理
 */
public class ClusterRuleCreateHandler extends AbstractRuleCreateHandler {

    @Override
    protected ActorRef createDestResource() {
        return context.actorOf(new ClusterRouterPool(new RoundRobinPool(0),
                        new ClusterRouterPoolSettings(EnvUtil.getClusterInstancesMax(), 1, true, new HashSet<>()))
                        .props(ResourceBroadcastActor.props(rule.getDestResourceList(), ruleActorRef)),
                "dest_broadcast");
    }

    @Override
    protected LinkedList<ActorRef> createTransform(ActorRef destResourceActorRef) {
        LinkedList<ActorRef> transformActorRefList = new LinkedList<>();
        List<Transform> transformList = ListUtil.reverse(rule.getTransformList());
        ActorRef next = destResourceActorRef;
        for (Transform transform : transformList) {
            ActorRef transformActor = context.actorOf(new ClusterRouterPool(new RoundRobinPool(0),
                            new ClusterRouterPoolSettings(EnvUtil.getClusterInstancesMax(), transform.getWorkerNum(), true, new HashSet<>()))
                            .props(TransformActor.props(transform, ruleActorRef, next)),
                    transform.getTransformRuntimeId());
            transformActorRefList.add(transformActor);
            next = transformActor;
        }
        return transformActorRefList;
    }

    @Override
    protected void createSourceResource(LinkedList<ActorRef> transformActorRefList) {
        // 创建源actor 绑定第一个转换
        ActorRef transformActorRef = transformActorRefList.getLast();
        rule.getSourceResourceList().forEach(sourceResource -> {
            SourceModeEnum mode = sourceResource.getResourceType().getMode(sourceResource.getProperties());

            switch (mode) {
                case SUBSCRIBE_SINGLE: {
                    // 集群下仅有一个源
                    context.actorOf((ResourceActor.props(sourceResource, DriverModeEnum.SOURCE, ruleActorRef, transformActorRef)),
                            sourceResource.getResourceRuntimeId());
                    break;
                }
                case SUBSCRIBE_MULTI:
                case LISTEN:
                {
                    // 集群下每个节点都创建一个源
                    context.actorOf(new ClusterRouterPool(new RoundRobinPool(0),
                                    new ClusterRouterPoolSettings(EnvUtil.getClusterInstancesMax(), 1, true, new HashSet<>()))
                                    .props(ResourceActor.props(sourceResource, DriverModeEnum.SOURCE, ruleActorRef, transformActorRef)),
                            sourceResource.getResourceRuntimeId());
                    break;
                }
                case SCHEDULE: {
                    // 集群下每个节点都创建一个源
                    ActorRef actorRef = context.actorOf(new ClusterRouterPool(new RoundRobinPool(0),
                                    new ClusterRouterPoolSettings(EnvUtil.getClusterInstancesMax(), 1, true, new HashSet<>()))
                                    .props(ResourceActor.props(sourceResource, DriverModeEnum.SOURCE, ruleActorRef, transformActorRef)),
                            sourceResource.getResourceRuntimeId());
                    createSchedule(sourceResource, actorRef);
                }
            }
        });
    }


}
