package com.leon.datalink.resource.util.mqtt.client;

import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.utils.SSLUtils;
import com.leon.datalink.resource.util.mqtt.MqttClientConfig;
import com.leon.datalink.resource.util.mqtt.MqttClientFactory;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public class MqttClientV3 implements IMqttClient {

    private MqttClient mqttClient;

    @Override
    public void connect(MqttClientConfig mqttClientConfig) throws Exception {
        mqttClient = new MqttClient(mqttClientConfig.getHostUrl(), UUID.randomUUID().toString(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        // mqtt版本
        options.setMqttVersion(mqttClientConfig.getMqttVersion());
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
        // 是否开启ssl
        if (mqttClientConfig.getSsl()) {
            InputStream resourceAsStream = MqttClientFactory.class.getClassLoader().getResourceAsStream(EnvUtil.getCaCrtFile());
            options.setSocketFactory(SSLUtils.getSocketFactory(resourceAsStream));
        }
        // 连接服务器
        mqttClient.connect(options);
    }

    @Override
    public void publish(String topic, byte[] payload, int qosLevel, boolean isRetain, Map<String, String> userProperties) throws Exception {
        mqttClient.publish(topic, payload, qosLevel, isRetain);
    }

    @Override
    public void subscribe(MqttSubParam[] subParams) throws Exception {
        String[] topic = new String[subParams.length];
        int[] qos = new int[subParams.length];
        for (int i = 0; i < subParams.length; i++) {
            topic[i] = subParams[i].getTopic();
            qos[i] = subParams[i].getQos();
        }
        mqttClient.subscribe(topic, qos);
    }

    @Override
    public boolean isConnected() {
        return mqttClient.isConnected();
    }

    @Override
    public void disconnect() throws Exception {
        mqttClient.disconnect();
    }

    @Override
    public void close() throws Exception {
        mqttClient.close();
    }

    @Override
    public void setCallback(IMqttCallback mqttCallback) {
        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                mqttCallback.exceptionOccurred(cause.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                mqttCallback.messageArrived(topic, message.getPayload(), message.getQos(), message.isRetained(), null);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

    }

}
