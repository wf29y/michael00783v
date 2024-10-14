package com.leon.datalink.driver.actor;

import akka.actor.ActorRef;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverModeEnum;

import java.util.Map;

public class DriverCreateMsg {

    private Class<? extends Driver> driverClass;

    private DriverModeEnum driverMode;

    private Map<String, Object> properties;

    private ActorRef ruleActorRef;

    public DriverCreateMsg(Class<? extends Driver> driverClass, DriverModeEnum driverMode, Map<String, Object> properties,ActorRef ruleActorRef) {
        this.driverClass = driverClass;
        this.driverMode = driverMode;
        this.properties = properties;
        this.ruleActorRef = ruleActorRef;
    }

    public Class<? extends Driver> getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(Class<? extends Driver> driverClass) {
        this.driverClass = driverClass;
    }

    public DriverModeEnum getDriverMode() {
        return driverMode;
    }

    public void setDriverMode(DriverModeEnum driverMode) {
        this.driverMode = driverMode;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public ActorRef getRuleActorRef() {
        return ruleActorRef;
    }

    public void setRuleActorRef(ActorRef ruleActorRef) {
        this.ruleActorRef = ruleActorRef;
    }
}
