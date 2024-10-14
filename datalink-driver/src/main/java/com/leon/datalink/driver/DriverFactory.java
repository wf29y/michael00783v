
package com.leon.datalink.driver;

import akka.actor.ActorRef;

import java.lang.reflect.Constructor;
import java.util.Map;


/**
 * DriverFactory
 * @author leon
 */
public class DriverFactory {

    public static Driver getDriver(Class<? extends Driver> driverClass, Map<String, Object> properties) throws Exception {
        if (driverClass == null) return null;
        Constructor<? extends Driver> constructor = driverClass.getDeclaredConstructor(Map.class);
        return constructor.newInstance(properties);
    }

    public static Driver getDriver(Class<? extends Driver> driverClass, Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef) throws Exception {
        if (driverClass == null) return null;
        Constructor<? extends Driver> constructor = driverClass.getDeclaredConstructor(Map.class, DriverModeEnum.class,ActorRef.class);
        return constructor.newInstance(properties, driverMode,ruleActorRef);
    }

}
