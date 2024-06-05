package com.leon.datalink.web.config;

import akka.actor.ActorSystem;
import akka.event.Logging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName DatalinkConfig
 * @Description
 * @Author Leon
 * @Date2022/4/8 15:07
 * @Version V1.0
 **/
@Configuration
public class DatalinkConfig {
    @Bean
    public ActorSystem actorSystem() {
        ActorSystem actorSystem = ActorSystem.create("datalink");
        actorSystem.eventStream().setLogLevel(Logging.ErrorLevel());
        return actorSystem;
    }
}
