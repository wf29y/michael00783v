package com.leon.datalink.cluster.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClusterConfig {

    private static final String memberList = System.getProperty("datalink.cluster.member.list");

    private static String localMemberName;

    public static Config getConfig() {
        String[] memberArray = memberList.split(",");
        String[] local = memberArray[0].split(":");

        localMemberName = "datalink@" + memberArray[0];

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

    public static String getLocalMemberName() {
        return localMemberName;
    }

}
