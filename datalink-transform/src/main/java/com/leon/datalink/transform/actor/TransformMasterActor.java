package com.leon.datalink.transform.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.routing.ActorRefRoutee;
import akka.routing.RoundRobinRoutingLogic;
import akka.routing.Routee;
import akka.routing.Router;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.runtime.entity.RuntimeData;
import com.leon.datalink.transform.Transform;

import java.util.ArrayList;

public class TransformMasterActor extends AbstractActor {

    private final Transform transform;

    private final ActorRef nextActor;

    public Router router;

    public TransformMasterActor(Transform transform, ActorRef nextActor) throws Exception {
        this.transform = transform;
        this.nextActor = nextActor;
    }

    @Override
    public void preStart() throws Exception {
        Loggers.DRIVER.info("start transform [{}]", getSelf().path());

        ArrayList<Routee> workerList = new ArrayList<>();
        for (int i = 1; i <= transform.getWorkerNum(); i++) {
            String name = String.format("%s_worker_%s", transform.getTransformRuntimeId(), i);
            ActorRef transformWorkerActor = getContext().actorOf(Props.create(TransformWorkerActor.class, transform, nextActor), name);
            getContext().watch(transformWorkerActor);//监听
            workerList.add(new ActorRefRoutee(transformWorkerActor));
        }
        /*
         * RoundRobinRoutingLogic: 轮询
         * BroadcastRoutingLogic: 广播
         * RandomRoutingLogic: 随机
         * SmallestMailboxRoutingLogic: 空闲
         */
        router = new Router(new RoundRobinRoutingLogic(), workerList);
    }

    @Override
    public void postStop() throws Exception {
        Loggers.DRIVER.info("stop transform [{}]", getSelf().path());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(RuntimeData.class, dataRecord -> {
            router.route(dataRecord, getSender());//进行路由转发
        }).build();
    }
}
