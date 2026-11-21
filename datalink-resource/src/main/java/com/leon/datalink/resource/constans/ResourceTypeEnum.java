package com.leon.datalink.resource.constans;


import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.StringUtils;
import com.leon.datalink.resource.Driver;
import com.leon.datalink.resource.driver.*;
import com.leon.datalink.resource.mode.SourceMode;

public enum ResourceTypeEnum {

    /**
     * 订阅型
     */
    MQTT(MqttDriver.class, prop -> prop.getBoolean("share", false) ? SourceModeEnum.SUBSCRIBE_MULTI : SourceModeEnum.SUBSCRIBE_SINGLE),
    KAFKA(KafkaDriver.class, prop -> StringUtils.isEmpty(prop.getString("group")) ? SourceModeEnum.SUBSCRIBE_SINGLE : SourceModeEnum.SUBSCRIBE_MULTI),
    PULSAR(PulsarDriver.class, prop -> prop.getString("subscriptionType", "Exclusive").equals("Exclusive") ? SourceModeEnum.SUBSCRIBE_SINGLE : SourceModeEnum.SUBSCRIBE_MULTI),
    ROCKETMQ(RocketMQDriver.class, prop -> prop.getString("model", "BROADCASTING").equals("BROADCASTING") ? SourceModeEnum.SUBSCRIBE_SINGLE : SourceModeEnum.SUBSCRIBE_MULTI),
    ACTIVEMQ(ActiveMQDriver.class, prop -> prop.getString("model", "topic").equals("topic") ? SourceModeEnum.SUBSCRIBE_SINGLE : SourceModeEnum.SUBSCRIBE_MULTI),
    RABBITMQ(RabbitMQDriver.class, prop -> SourceModeEnum.SUBSCRIBE_MULTI),

    /**
     * 监听型
     */
    TCP(TcpDriver.class, prop -> SourceModeEnum.LISTEN),
    UDP(UdpDriver.class, prop -> SourceModeEnum.LISTEN),
    HTTPSERVER(HttpServerDriver.class, prop -> SourceModeEnum.LISTEN),
    COAPSERVER(CoapServerDriver.class, prop -> SourceModeEnum.LISTEN),

    /**
     * 定时调度型
     */
    HTTPCLIENT(HttpClientDriver.class, prop -> SourceModeEnum.SCHEDULE),
    COAPCLIENT(CoapClientDriver.class, prop -> SourceModeEnum.SCHEDULE),
    MYSQL(MysqlDriver.class, prop -> SourceModeEnum.SCHEDULE),
    POSTGRESQL(PostgresqlDriver.class, prop -> SourceModeEnum.SCHEDULE),
    TDENGINE(TDengineDriver.class, prop -> SourceModeEnum.SCHEDULE),
    SQLSERVER(SqlServerDriver.class, prop -> SourceModeEnum.SCHEDULE),
    OPCUA(OpcUADriver.class, prop -> SourceModeEnum.SCHEDULE),
    REDIS(RedisDriver.class, prop -> SourceModeEnum.SCHEDULE),
    SNMP(SnmpDriver.class, prop -> SourceModeEnum.SCHEDULE),
    MODBUSTCP(ModbusTcpDriver.class, prop -> SourceModeEnum.SCHEDULE),
    TIMESCALEDB(TimescaleDBDriver.class, prop -> SourceModeEnum.SCHEDULE),
    MARIADB(MariaDBDriver.class, prop -> SourceModeEnum.SCHEDULE),
    DM8(DM8Driver.class, prop -> SourceModeEnum.SCHEDULE),
    KINGBASE(KingbaseDriver.class, prop -> SourceModeEnum.SCHEDULE);


    private final Class<? extends Driver> driver;

    private final SourceMode mode;

    ResourceTypeEnum(Class<? extends Driver> driver, SourceMode mode) {
        this.driver = driver;
        this.mode = mode;
    }

    public Class<? extends Driver> getDriver() {
        return driver;
    }

    public SourceModeEnum getMode(ConfigProperties configProperties) {
        return mode.get(configProperties);
    }
}
