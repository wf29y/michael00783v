package com.leon.datalink.web.config;

import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.event.Logging;
import com.leon.datalink.cluster.actor.ClusterListenerActor;
import com.leon.datalink.cluster.config.ClusterConfig;
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

    @Bean
    public ActorSystem actorSystem() {
        ActorSystem actorSystem;
        if (EnvUtil.isCluster()) {
            actorSystem = ActorSystem.create("datalink", ClusterConfig.getConfig());
            actorSystem.actorOf(Props.create(ClusterListenerActor.class), "datalinkCluster");
        } else {
            actorSystem = ActorSystem.create("datalink");
        }
        actorSystem.eventStream().setLogLevel(Logging.ErrorLevel());
        return actorSystem;
    }




}
