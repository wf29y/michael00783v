package com.leon.datalink.resource.util.mqtt;

import com.leon.datalink.resource.util.mqtt.client.IMqttClient;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class MqttTemplate extends GenericObjectPool<IMqttClient> {

    public MqttTemplate(PooledObjectFactory<IMqttClient> factory, GenericObjectPoolConfig<IMqttClient> config) {
        super(factory, config);
    }

    public void publish(String topic, byte[] payload, int qosLevel, boolean isRetain) throws Exception {
        IMqttClient client = null;
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
