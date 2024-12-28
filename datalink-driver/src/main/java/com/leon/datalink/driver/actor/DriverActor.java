package com.leon.datalink.driver.actor;

import akka.actor.AbstractActor;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverFactory;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;

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

    public DriverActor(Class<? extends Driver> driverClass, Map<String, Object> properties, DriverModeEnum driverMode, String ruleId) throws Exception {
        this.driver = DriverFactory.getDriver(driverClass, properties, driverMode, getContext().getParent(), ruleId);
    }

    @Override
    public void preStart() {
        Loggers.DRIVER.info("start driver [{}]", getSelf().path());
        try {
            driver.create();
        } catch (Exception e) {
            Loggers.DRIVER.error("driver actor start error: {}", e.getMessage());
        }
    }

    @Override
    public void postStop() {
        Loggers.DRIVER.info("stop  driver [{}]", getSelf().path());
        try {
            driver.destroy();
        } catch (Exception e) {
            Loggers.DRIVER.error("driver actor stop error: {}", e.getMessage());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(PublishDataMsg.class, msg -> {
                    RuntimeUpdateDataMsg runtimeUpdateDataMsg = msg.getRuntimeUpdateDataMsg();
                    try {
                        Object publishData = driver.handleData(msg.getData());
                        runtimeUpdateDataMsg.setPublishData(publishData);
                        runtimeUpdateDataMsg.setPublishSuccess(true);
                        runtimeUpdateDataMsg.setMessage("success");
                    } catch (Exception e) {
                        Loggers.DRIVER.error("driver actor handle data error: {}", e.getMessage());
                        runtimeUpdateDataMsg.setPublishSuccess(false);
                        runtimeUpdateDataMsg.setMessage(e.getMessage());
                    } finally {
                        msg.getRuntimeRef().tell(runtimeUpdateDataMsg, getSelf());
                    }
                }
        ).build();
    }


}
