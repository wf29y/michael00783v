package com.leon.datalink.resource.util.mqtt.client;

import com.leon.datalink.resource.util.mqtt.MqttClientConfig;

import java.util.Map;

public interface IMqttClient {

    void connect(MqttClientConfig mqttClientConfig) throws Exception;

    void publish(String topic, byte[] payload, int qosLevel, boolean isRetain, Map<String, String> userProperties) throws Exception;

    void subscribe(MqttSubParam[] subParams) throws Exception;

    boolean isConnected();

    void disconnect() throws Exception;

    void close() throws Exception;

    void setCallback(IMqttCallback mqttCallback);

}
