package com.leon.datalink.driver.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverFactory;
import com.leon.datalink.driver.constans.DriverModeEnum;

import java.util.Map;

/**
 * @ClassName DriverActor
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:05
 * @Version V1.0
 **/
public class DriverActor extends AbstractActor {

    private final Driver driver;

    public DriverActor(Class<? extends Driver> driverClass, Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef) throws Exception {
        this.driver = DriverFactory.getDriver(driverClass, properties, driverMode, ruleActorRef);
    }

    @Override
    public void preStart() {
        Loggers.DRIVER.info("driver actor start");
        try {
            driver.create();
        } catch (Exception e) {
            Loggers.DRIVER.error("driver actor start error: {}", e.getMessage());
        }
    }

    @Override
    public void postStop() {
        Loggers.DRIVER.info("driver actor stop");
        try {
            driver.destroy();
        } catch (Exception e) {
            Loggers.DRIVER.error("driver actor stop error: {}", e.getMessage());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DriverDataMsg.class, msg -> driver.handleData(msg.getData())).build();
    }


}
