package com.leon.datalink.resource.util.mqtt.client;

import com.leon.datalink.resource.util.mqtt.MqttClientConfig;

public interface IMqttClient {

    void connect(MqttClientConfig mqttClientConfig) throws Exception;

    void publish(String topic, byte[] payload, int qosLevel, boolean isRetain) throws Exception;

    void subscribe(String topicFilter, int qos) throws Exception;

    void subscribe(String[] topicFilters, int[] qos) throws Exception;

    boolean isConnected();

    void disconnect() throws Exception;

    void close() throws Exception;

    void setCallback(IMqttCallback mqttCallback);

}
