package com.leon.datalink.driver.actor;

import akka.actor.AbstractActor;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverFactory;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.entity.DriverProperties;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;

/**
 * @ClassName DriverActor
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:05
 * @Version V1.0
 **/
public class DriverActor extends AbstractActor {

    private final Driver driver;

    private final DriverModeEnum driverMode;

    private final DriverProperties driverProperties;

    public DriverActor(Class<? extends Driver> driverClass, DriverProperties driverProperties, DriverModeEnum driverMode, String ruleId) throws Exception {
        this.driverMode = driverMode;
        this.driverProperties = driverProperties;
        this.driver = DriverFactory.getDriver(driverClass);
        this.driver.init(getContext().getParent(), ruleId);
    }

    @Override
    public void preStart() {
        Loggers.DRIVER.info("start driver [{}]", getSelf().path());
        try {
            driver.create(driverMode, driverProperties);
        } catch (Exception e) {
            e.printStackTrace();
            Loggers.DRIVER.error("driver actor start error {} : {}", driver.getClass(), e.getMessage());
        }
    }

    @Override
    public void postStop() {
        Loggers.DRIVER.info("stop  driver [{}]", getSelf().path());
        try {
            driver.destroy(driverMode, driverProperties);
        } catch (Exception e) {
            Loggers.DRIVER.error("driver actor stop error {} : {}", driver.getClass(), e.getMessage());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(PublishDataMsg.class, msg -> {
                    RuntimeUpdateDataMsg runtimeUpdateDataMsg = msg.getRuntimeUpdateDataMsg();
                    try {
                        Object publishData = driver.handleData(msg.getData(), driverProperties);
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
