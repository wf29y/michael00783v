package com.leon.datalink.resource.util.mqtt.client;

public interface IMqttCallback {

    void exceptionOccurred(String exceptionMessage);

    void messageArrived(String topic, byte[] payload, int qos, boolean retained);

}
