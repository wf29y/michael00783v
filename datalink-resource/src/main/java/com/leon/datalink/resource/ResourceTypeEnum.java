package com.leon.datalink.resource;


import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.impl.*;

public enum ResourceTypeEnum {

    MQTT(MqttDriver.class),
    KAFKA(KafkaDriver.class),
    HTTP(HttpDriver.class),
    MYSQL(MysqlDriver .class),
    POSTGRESQL(PostgresqlDriver.class),
    TDENGINE(TDengineDriver.class),
    SQLSERVER(SqlServerDriver.class);

    private final Class<? extends Driver> driver;

    ResourceTypeEnum(Class<? extends Driver> driver) {
        this.driver = driver;
    }

    public Class<? extends Driver> getDriver() {
        return driver;
    }

}
