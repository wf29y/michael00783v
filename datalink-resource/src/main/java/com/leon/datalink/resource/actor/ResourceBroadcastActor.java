package com.leon.datalink.resource.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.BroadcastRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.leon.datalink.resource.entity.Resource;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;


import java.util.List;
import java.util.stream.Collectors;

/**
 * 目的资源广播
 */
public class ResourceBroadcastActor extends AbstractActor {

    private Router router;

    private final List<Resource> resourceList;

    private final ActorRef ruleActorRef;

    public static Props props(List<Resource> resourceList, ActorRef ruleActorRef) {
        return Props.create(ResourceBroadcastActor.class, () -> new ResourceBroadcastActor(resourceList, ruleActorRef));
    }

    public ResourceBroadcastActor(List<Resource> resourceList, ActorRef ruleActorRef) throws Exception {
        this.resourceList = resourceList;
        this.ruleActorRef = ruleActorRef;
    }

    @Override
    public void preStart() throws Exception {
        List<Routee> RouteeList = resourceList.stream().map(destResource -> {
            ActorRef destResourceActor = getContext().actorOf((ResourceActor.props(destResource, DriverModeEnum.DEST, ruleActorRef)), destResource.getResourceRuntimeId());
            getContext().watch(destResourceActor);
            return new ActorRefRoutee(destResourceActor);
        }).collect(Collectors.toList());
        // 广播
        router = new Router(new BroadcastRoutingLogic(), RouteeList);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuntimeData.class, dataRecord -> {
            // 来自TRANSFORM的数据 广播转发给所有目的地actor
            if (dataRecord.getType() == RuntimeTypeEnum.TRANSFORM) {
                router.route(dataRecord, getSelf());
            }
        }).build();
    }
}
