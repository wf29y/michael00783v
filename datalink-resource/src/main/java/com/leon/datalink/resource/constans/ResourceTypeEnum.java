package com.leon.datalink.resource.constans;


import com.leon.datalink.resource.Driver;
import com.leon.datalink.resource.driver.*;

public enum ResourceTypeEnum {

    MQTT(MqttDriver.class, SourceModeEnum.SUBSCRIBE),
    KAFKA(KafkaDriver.class, SourceModeEnum.SUBSCRIBE),
    RABBITMQ(RabbitMQDriver.class, SourceModeEnum.SUBSCRIBE),
    HTTPCLIENT(HttpClientDriver.class, SourceModeEnum.SCHEDULE),
    HTTPSERVER(HttpServerDriver.class, SourceModeEnum.LISTEN),
    MYSQL(MysqlDriver.class, SourceModeEnum.SCHEDULE),
    POSTGRESQL(PostgresqlDriver.class, SourceModeEnum.SCHEDULE),
    TDENGINE(TDengineDriver.class, SourceModeEnum.SCHEDULE),
    SQLSERVER(SqlServerDriver.class, SourceModeEnum.SCHEDULE),
    OPCUA(OpcUADriver.class, SourceModeEnum.SCHEDULE),
    REDIS(RedisDriver.class, SourceModeEnum.SCHEDULE),
    TCP(TcpDriver.class, SourceModeEnum.LISTEN),
    UDP(UdpDriver.class, SourceModeEnum.LISTEN),
    SNMP(SnmpDriver.class, SourceModeEnum.SCHEDULE),
    MODBUSTCP(ModbusTcpDriver.class, SourceModeEnum.SCHEDULE),
    TIMESCALEDB(TimescaleDBDriver.class, SourceModeEnum.SCHEDULE),
    MARIADB(MariaDBDriver.class, SourceModeEnum.SCHEDULE),
    ROCKETMQ(RocketMQDriver.class, SourceModeEnum.SUBSCRIBE),
    ACTIVEMQ(ActiveMQDriver.class, SourceModeEnum.SUBSCRIBE),
    PULSAR(PulsarDriver.class, SourceModeEnum.SUBSCRIBE),
    DM8(DM8Driver.class, SourceModeEnum.SCHEDULE);

    private final Class<? extends Driver> driver;

    private final SourceModeEnum mode;

    ResourceTypeEnum(Class<? extends Driver> driver, SourceModeEnum mode) {
        this.driver = driver;
        this.mode = mode;
    }

    public Class<? extends Driver> getDriver() {
        return driver;
    }

    public SourceModeEnum getMode() {
        return mode;
    }
}
