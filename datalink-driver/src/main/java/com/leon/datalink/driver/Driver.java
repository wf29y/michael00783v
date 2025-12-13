package com.leon.datalink.driver;

import akka.actor.ActorRef;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.core.config.ConfigProperties;

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
    void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception;

    /**
     * destroy client
     */
    void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception;

    /**
     * test ok
     */
    boolean test(ConfigProperties properties);

    /**
     * handle receive data
     */
    Object handleData(Object data, ConfigProperties properties) throws Exception;


}
