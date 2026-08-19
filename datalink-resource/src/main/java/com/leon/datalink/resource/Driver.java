package com.leon.datalink.resource;

import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.runtime.entity.RuntimeData;

import java.util.function.Consumer;

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
    void init(Consumer<RuntimeData> consumer);

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
     * handle collect data
     */
    void scheduleTrigger(ConfigProperties properties) throws Exception;

    /**
     * handle receive data
     */
    Object handleData(Object data, ConfigProperties properties) throws Exception;


}
