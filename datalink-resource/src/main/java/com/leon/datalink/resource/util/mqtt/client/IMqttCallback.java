package com.leon.datalink.resource.util.mqtt.client;

import java.util.Map;

public interface IMqttCallback {

    void exceptionOccurred(String exceptionMessage);

    void messageArrived(String topic, byte[] payload, int qos, boolean retained, Map<String,String> userProperties);

}
