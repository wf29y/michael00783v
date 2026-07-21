package com.leon.datalink.rule.handler.impl;

import akka.actor.ActorRef;
import akka.cluster.routing.ClusterRouterPool;
import akka.cluster.routing.ClusterRouterPoolSettings;
import akka.routing.RoundRobinPool;
import cn.hutool.core.collection.ListUtil;
import com.leon.datalink.resource.actor.ResourceActor;
import com.leon.datalink.resource.actor.ResourceBroadcastActor;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.resource.constans.ResourceClusterModeEnum;
import com.leon.datalink.rule.handler.AbstractRuleStartHandler;
import com.leon.datalink.transform.Transform;
import com.leon.datalink.transform.actor.TransformActor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import static com.leon.datalink.rule.constants.Constants.CLUSTER_MAX_TOTAL_INSTANCES;

/**
 * 集群模式下规则启动处理
 */
public class ClusterRuleStartHandler extends AbstractRuleStartHandler {

    @Override
    protected ActorRef createDestResource() {
        return context.actorOf(new ClusterRouterPool(new RoundRobinPool(0),
                        new ClusterRouterPoolSettings(CLUSTER_MAX_TOTAL_INSTANCES, 1, true, new HashSet<>()))
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
                            new ClusterRouterPoolSettings(CLUSTER_MAX_TOTAL_INSTANCES, transform.getWorkerNum(), true, new HashSet<>()))
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
            ResourceClusterModeEnum mode = sourceResource.getResourceType().getMode();
            if (ResourceClusterModeEnum.CLUSTER.equals(mode)) {
                // 集群下每个节点都创建一个源
                context.actorOf(new ClusterRouterPool(new RoundRobinPool(0),
                                new ClusterRouterPoolSettings(CLUSTER_MAX_TOTAL_INSTANCES, 1, true, new HashSet<>()))
                                .props(ResourceActor.props(sourceResource, DriverModeEnum.SOURCE, ruleActorRef, transformActorRef)),
                        sourceResource.getResourceRuntimeId());
            } else if (ResourceClusterModeEnum.SINGLETON.equals(mode)) {
                // 集群下仅有一个源
                context.actorOf((ResourceActor.props(sourceResource, DriverModeEnum.SOURCE, ruleActorRef, transformActorRef)),
                        sourceResource.getResourceRuntimeId());
            }
        });
    }


}
