package com.leon.datalink.driver.actor;

import akka.actor.AbstractActor;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverFactory;

/**
 * @ClassNameServiceActor
 * @Description
 * @Author Solley
 * @Date2022/4/8 15:05
 * @Version V1.0
 **/
public class DriverActor extends AbstractActor {

    Driver driver;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DriverCreateMsg.class, msg -> {
                    driver = DriverFactory.getDriver(msg.getDriverClass(), msg.getProperties(), msg.getDriverMode(), msg.getRuleActorRef());
                    driver.create();
                })
                .match(DriverCloseMsg.class, msg -> {
                    driver.destroy();
                    getContext().getSystem().stop(getSelf());
                })
                .match(DriverDataMsg.class, msg -> driver.handleData(msg.getData()))
                .build();
    }


}
