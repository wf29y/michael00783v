package com.leon.datalink.resource.util.mqtt.client;

import com.leon.datalink.resource.util.mqtt.MqttClientConfig;
import com.leon.datalink.resource.util.mqtt.entity.MqttMessageEntity;
import com.leon.datalink.resource.util.mqtt.entity.MqttSubParam;

public interface IMqttClient {

    void connect(MqttClientConfig mqttClientConfig) throws Exception;

    void publish(MqttMessageEntity message) throws Exception;

    void subscribe(MqttSubParam[] subParams) throws Exception;

    boolean isConnected();

    void disconnect() throws Exception;

    void close() throws Exception;

    void setCallback(IMqttCallback mqttCallback);

}
