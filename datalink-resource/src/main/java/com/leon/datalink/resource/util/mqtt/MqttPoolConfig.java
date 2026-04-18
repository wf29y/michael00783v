package com.leon.datalink.resource.util.mqtt;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;


public class MqttPoolConfig  extends GenericObjectPoolConfig<MqttClient> {

    public MqttPoolConfig() {
        this.setMinEvictableIdleTimeMillis(60000L);
        this.setTimeBetweenEvictionRunsMillis(30000L);
        this.setNumTestsPerEvictionRun(-1);
        this.setBlockWhenExhausted(true);
        this.setTestOnBorrow(true);
        this.setTestOnReturn(true);
        this.setTestWhileIdle(true);
        this.setJmxEnabled(false);
    }

}
