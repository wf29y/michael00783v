package com.leon.datalink.resource.constans;


import com.leon.datalink.resource.Driver;
import com.leon.datalink.resource.driver.*;

public enum ResourceTypeEnum {

    MQTT(MqttDriver.class, ResourceClusterModeEnum.SINGLETON),
    KAFKA(KafkaDriver.class, ResourceClusterModeEnum.SINGLETON),
    RABBITMQ(RabbitMQDriver.class, ResourceClusterModeEnum.SINGLETON),
    HTTPCLIENT(HttpClientDriver.class, ResourceClusterModeEnum.SINGLETON),
    HTTPSERVER(HttpServerDriver.class, ResourceClusterModeEnum.CLUSTER),
    MYSQL(MysqlDriver.class, ResourceClusterModeEnum.SINGLETON),
    POSTGRESQL(PostgresqlDriver.class, ResourceClusterModeEnum.SINGLETON),
    TDENGINE(TDengineDriver.class, ResourceClusterModeEnum.SINGLETON),
    SQLSERVER(SqlServerDriver.class, ResourceClusterModeEnum.SINGLETON),
    OPCUA(OpcUADriver.class, ResourceClusterModeEnum.SINGLETON),
    REDIS(RedisDriver.class, ResourceClusterModeEnum.SINGLETON),
    TCP(TcpDriver.class, ResourceClusterModeEnum.CLUSTER),
    UDP(UdpDriver.class, ResourceClusterModeEnum.CLUSTER),
    SNMP(SnmpDriver.class, ResourceClusterModeEnum.SINGLETON),
    MODBUSTCP(ModbusTcpDriver.class, ResourceClusterModeEnum.SINGLETON),
    TIMESCALEDB(TimescaleDBDriver.class, ResourceClusterModeEnum.SINGLETON),
    MARIADB(MariaDBDriver.class, ResourceClusterModeEnum.SINGLETON),
    ROCKETMQ(RocketMQDriver.class, ResourceClusterModeEnum.SINGLETON),
    ACTIVEMQ(ActiveMQDriver.class, ResourceClusterModeEnum.SINGLETON),
    PULSAR(PulsarDriver.class, ResourceClusterModeEnum.SINGLETON);

    private final Class<? extends Driver> driver;

    private final ResourceClusterModeEnum mode;

    ResourceTypeEnum(Class<? extends Driver> driver, ResourceClusterModeEnum mode) {
        this.driver = driver;
        this.mode = mode;
    }

    public Class<? extends Driver> getDriver() {
        return driver;
    }

    public ResourceClusterModeEnum getMode() {
        return mode;
    }
}
