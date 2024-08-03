package com.leon.datalink.resource;


import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.impl.MqttDriver;
import com.leon.datalink.driver.impl.MysqlDriver;

public enum ResourceTypeEnum {

    MQTT(MqttDriver.class),
    MYSQL(MysqlDriver .class);

    private final Class<? extends Driver> driver;

    ResourceTypeEnum(Class<? extends Driver> driver) {
        this.driver = driver;
    }

    public Class<? extends Driver> getDriver() {
        return driver;
    }

}
