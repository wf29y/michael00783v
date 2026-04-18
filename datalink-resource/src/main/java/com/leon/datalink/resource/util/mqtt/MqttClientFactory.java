package com.leon.datalink.resource.util.mqtt;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.UUID;

public class MqttClientFactory extends BasePooledObjectFactory<MqttClient> {

    private final MqttClientConfig mqttClientConfig;

    public MqttClientFactory(MqttClientConfig mqttClientConfig) {
        this.mqttClientConfig = mqttClientConfig;
    }

    @Override
    public MqttClient create() throws MqttException {
        MqttClient mqttClient = new MqttClient(mqttClientConfig.getHostUrl(), UUID.randomUUID().toString(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        // 如果想要断线这段时间的数据，要设置成false，并且重连后不用再次订阅，否则不会得到断线时间的数据
        options.setCleanSession(true);
        // 增加 actualInFlight 的值
        options.setMaxInflight(1000);
        // 自动重连
        options.setAutomaticReconnect(true);
        // 设置连接的用户名
        options.setUserName(mqttClientConfig.getUsername());
        // 设置连接的密码
        options.setPassword(mqttClientConfig.getPassword().toCharArray());
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(mqttClientConfig.getConnectionTimeout());
        // 设置会话心跳时间 单位为秒
        options.setKeepAliveInterval(mqttClientConfig.getKeepAliveInterval());
        // 连接服务器
        mqttClient.connect(options);
        return mqttClient;
    }

    @Override
    public PooledObject<MqttClient> wrap(MqttClient mqttAsyncClient) {
        return new DefaultPooledObject<>(mqttAsyncClient);
    }

    @Override
    public void destroyObject(PooledObject<MqttClient> pooledMqttClient) throws Exception {
        if (pooledMqttClient != null && pooledMqttClient.getObject() != null && pooledMqttClient.getObject().isConnected()) {
            pooledMqttClient.getObject().disconnect();
        }
    }

    @Override
    public boolean validateObject(PooledObject<MqttClient> pooledMqttClient) {
        return pooledMqttClient.getObject().isConnected();
    }
}
