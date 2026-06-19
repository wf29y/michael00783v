package com.leon.datalink.cluster;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import com.leon.datalink.cluster.actor.ClusterListenerActor;
import com.leon.datalink.core.utils.EnvUtil;
import com.leon.datalink.core.utils.StringUtils;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ActorSystemFactory
 * @author liyang
 */
public class ActorSystemFactory {

    /**
     * 创建ActorSystem
     * @param memberListConfig application.properties 成员配置
     * @return ActorSystem
     */
    public static ActorSystem create(String memberListConfig) {
        ActorSystem actorSystem;
        if (EnvUtil.isCluster()) {
            actorSystem = ActorSystem.create("datalink", getConfig(memberListConfig));
            actorSystem.actorOf(Props.create(ClusterListenerActor.class), "datalinkCluster");
        } else {
            actorSystem = ActorSystem.create("datalink");
            ClusterMemberManager.setLocalMemberName("datalink@127.0.0.1");
        }
        actorSystem.eventStream().setLogLevel(Logging.ErrorLevel());
        return actorSystem;
    }

    /**
     * 获取配置
     */
    private static Config getConfig(String memberListConfig) {

        // 优先使用命令行里的成员配置
        String memberList = System.getProperty("datalink.cluster.member.list");
        if(StringUtils.isEmpty(memberList)) memberList = memberListConfig;

        String[] memberArray = memberList.split(",");
        String[] local = memberArray[0].split(":");

        String localMemberName = "datalink@" + memberArray[0];
        ClusterMemberManager.setLocalMemberName(localMemberName);

        Map<String, Object> map = new HashMap<>();
        map.put("akka.actor.provider", "cluster");

        map.put("akka.actor.allow-java-serialization", true);

        map.put("akka.actor.serializers.protostuff", "com.leon.datalink.core.serializer.ProtostuffSerializer");
        map.put("akka.actor.serialization-bindings.\"com.leon.datalink.core.serializer.ProtostuffSerializable\"", "protostuff");

        map.put("akka.remote.artery.enabled", "on");
        map.put("akka.remote.artery.transport", "tcp");
        map.put("akka.remote.artery.canonical.hostname", local[0]);
        map.put("akka.remote.artery.canonical.port", local[1]);

        List<String> nodes = Arrays.stream(memberArray).map(member -> "akka://datalink@" + member).collect(Collectors.toList());
        map.put("akka.cluster.seed-nodes", nodes);

        return ConfigFactory.parseMap(map);
    }

}
