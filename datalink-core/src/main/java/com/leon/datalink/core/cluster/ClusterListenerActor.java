package com.leon.datalink.core.cluster;


import akka.actor.AbstractActor;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import com.leon.datalink.core.utils.Loggers;

/**
 * 集群监听
 */
public class ClusterListenerActor extends AbstractActor {
    Cluster cluster = Cluster.get(getContext().getSystem());

    @Override
    public void preStart() throws Exception {
        cluster.subscribe(self(), ClusterEvent.initialStateAsEvents(), ClusterEvent.MemberEvent.class, ClusterEvent.UnreachableMember.class);
    }

    @Override
    public void postStop() throws Exception {
        cluster.unsubscribe(self());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(ClusterEvent.MemberUp.class, msg -> {
            ClusterMemberContent.up(msg.member());
            Loggers.CLUSTER.info("member up {}", msg.member().address());
        }).match(ClusterEvent.UnreachableMember.class, msg -> {
            Loggers.CLUSTER.info("member unreachable {}", msg.member().address());
        }).match(ClusterEvent.MemberRemoved.class, msg -> {
            ClusterMemberContent.down(msg.member());
            Loggers.CLUSTER.info("member removed {}", msg.member().address());
        })
//                .match(ClusterEvent.MemberEvent.class, msg -> {
//                    Loggers.CLUSTER.info("member event {} : {}", msg.member().address(), msg);
//                })
                .build();
    }
}
