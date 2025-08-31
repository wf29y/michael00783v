package com.leon.datalink.resource;


import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.impl.*;

public enum ResourceTypeEnum {

    MQTT(MqttDriver.class),
    KAFKA(KafkaDriver.class),
    RABBITMQ(RabbitMQDriver.class),
    HTTPCLIENT(HttpClientDriver.class),
    HTTPSERVER(HttpServerDriver.class),
    MYSQL(MysqlDriver.class),
    POSTGRESQL(PostgresqlDriver.class),
    TDENGINE(TDengineDriver.class),
    SQLSERVER(SqlServerDriver.class),
    OPCUA(OpcUADriver.class),
    REDIS(RedisDriver.class),
    TCP(TcpDriver.class);

    private final Class<? extends Driver> driver;

    ResourceTypeEnum(Class<? extends Driver> driver) {
        this.driver = driver;
    }

    public Class<? extends Driver> getDriver() {
        return driver;
    }

}
