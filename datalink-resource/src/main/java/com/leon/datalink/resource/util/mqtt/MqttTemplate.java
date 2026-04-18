package com.leon.datalink.resource.util.mqtt;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;


public class MqttTemplate extends GenericObjectPool<MqttClient> {

    public MqttTemplate(PooledObjectFactory<MqttClient> factory, GenericObjectPoolConfig<MqttClient> config) {
        super(factory, config);
    }

    public void publish(String topic, byte[] payload, int qosLevel, boolean isRetain) throws Exception {
        MqttClient client = null;
        try {
            client = super.borrowObject();
            if (client.isConnected()) {
                client.publish(topic, payload, qosLevel, isRetain);
            }
        } finally {
            if (client != null) {
                super.returnObject(client);
            }
        }
    }
}
