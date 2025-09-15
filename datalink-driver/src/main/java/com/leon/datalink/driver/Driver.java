package com.leon.datalink.driver;

import akka.actor.ActorRef;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.entity.DriverProperties;

/**
 * @ClassName Driver
 * @Description
 * @Author Leon
 * @Date 2022/8/4 15:10
 * @Version V1.0
 **/
public interface Driver {

    /**
     * init driver
     */
    void init(ActorRef ruleActorRef, String ruleId);

    /**
     * create driver
     */
    void create(DriverModeEnum driverMode, DriverProperties properties) throws Exception;

    /**
     * destroy client
     */
    void destroy(DriverModeEnum driverMode, DriverProperties properties) throws Exception;

    /**
     * test ok
     */
    boolean test(DriverProperties properties);

    /**
     * handle receive data
     */
    Object handleData(Object data, DriverProperties properties) throws Exception;


}
