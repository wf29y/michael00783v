package com.leon.datalink.web.config;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import com.leon.datalink.core.cluster.ClusterListenerActor;
import com.leon.datalink.core.utils.EnvUtil;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName AkkaConfig
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:07
 * @Version V1.0
 **/
@Configuration
public class AkkaConfig {

    @Value("${datalink.cluster.member.list}")
    private String memberList;

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem actorSystem;
        if (EnvUtil.isCluster()) {
            actorSystem = ActorSystem.create("datalink", createClusterConfig());
            actorSystem.actorOf(Props.create(ClusterListenerActor.class), "datalinkCluster");
        } else {
            actorSystem = ActorSystem.create("datalink");
        }
        actorSystem.eventStream().setLogLevel(Logging.ErrorLevel());
        return actorSystem;
    }

    private Config createClusterConfig() {
        String[] memberArray = memberList.split(",");
        String[] local = memberArray[0].split(":");

        Map<String, Object> map = new HashMap<>();
        map.put("akka.actor.provider", "cluster");
        map.put("akka.actor.allow-java-serialization", true);
        map.put("akka.remote.artery.enabled", "on");
        map.put("akka.remote.artery.transport", "tcp");
        map.put("akka.remote.artery.canonical.hostname", local[0]);
        map.put("akka.remote.artery.canonical.port", local[1]);

        List<String> nodes = Arrays.stream(memberArray).map(member -> "akka://datalink@" + member).collect(Collectors.toList());
        map.put("akka.cluster.seed-nodes", nodes);

        return ConfigFactory.parseMap(map);
    }


}
