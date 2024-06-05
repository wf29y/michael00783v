package com.leon.datalink.resource;


import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.mqtt.MqttDriver;

public enum ResourceTypeEnum {

    MQTT(MqttDriver.class);

    private final Class<? extends Driver> driver;

    ResourceTypeEnum(Class<? extends Driver> driver) {
        this.driver = driver;
    }

    public Class<? extends Driver> getDriver() {
        return driver;
    }

}
