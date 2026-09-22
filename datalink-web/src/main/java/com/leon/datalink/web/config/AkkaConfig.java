package com.leon.datalink.web.config;

import akka.actor.ActorSystem;
import com.leon.datalink.cluster.ActorSystemFactory;
import com.leon.datalink.cluster.distributed.ConsistencyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

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
        return ActorSystemFactory.create();
    }

    @PreDestroy
    public void destroy(){
        ConsistencyManager.onSystemDestroy();
    }

}
