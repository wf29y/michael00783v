package com.leon.datalink.resource.util.mqtt.client;

import com.leon.datalink.core.evn.EnvUtil;
import com.leon.datalink.core.utils.SSLUtils;
import com.leon.datalink.resource.util.mqtt.MqttClientConfig;
import com.leon.datalink.resource.util.mqtt.MqttClientFactory;
import org.eclipse.paho.mqttv5.client.*;
import org.eclipse.paho.mqttv5.client.persist.MemoryPersistence;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;
import org.eclipse.paho.mqttv5.common.packet.UserProperty;

import java.io.InputStream;
import java.util.*;

public class MqttClientV5 implements IMqttClient {

    private MqttClient mqttClient;

    @Override
    public void connect(MqttClientConfig mqttClientConfig) throws Exception {
        mqttClient = new MqttClient(mqttClientConfig.getHostUrl(), UUID.randomUUID().toString(), new MemoryPersistence());
        MqttConnectionOptions options = new MqttConnectionOptions();
        // 设置连接的用户名
        options.setUserName(mqttClientConfig.getUsername());
        // 设置连接的密码
        options.setPassword(mqttClientConfig.getPassword().getBytes());
        // 设置自动重连
        options.setAutomaticReconnect(true);
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(mqttClientConfig.getConnectionTimeout());
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
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
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload);
        mqttMessage.setQos(qosLevel);
        mqttMessage.setRetained(isRetain);

        if (null != userProperties && userProperties.size() > 0) {
            List<UserProperty> userPropertieList = new ArrayList<>();
            userProperties.forEach((key, value) -> userPropertieList.add(new UserProperty(key, value)));
            MqttProperties mqttProperties = new MqttProperties();
            mqttProperties.setUserProperties(userPropertieList);
            mqttMessage.setProperties(mqttProperties);
        }

        mqttClient.publish(topic, mqttMessage);
    }

    @Override
    public void subscribe(String topicFilter, int qos) throws Exception {
        mqttClient.subscribe(topicFilter, qos);
    }

    @Override
    public void subscribe(String[] topicFilters, int[] qos) throws Exception {
        mqttClient.subscribe(topicFilters, qos);
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
            public void disconnected(MqttDisconnectResponse mqttDisconnectResponse) {
                mqttCallback.exceptionOccurred(mqttDisconnectResponse.getReasonString());
            }

            @Override
            public void mqttErrorOccurred(MqttException e) {
                mqttCallback.exceptionOccurred(e.getMessage());
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                HashMap<String, String> userProperties = new HashMap<>();
                message.getProperties().getUserProperties().forEach(userProperty -> userProperties.put(userProperty.getKey(), userProperty.getValue()));
                mqttCallback.messageArrived(topic, message.getPayload(), message.getQos(), message.isRetained(), userProperties);
            }

            @Override
            public void deliveryComplete(IMqttToken iMqttToken) {

            }

            @Override
            public void connectComplete(boolean b, String s) {

            }

            @Override
            public void authPacketArrived(int i, MqttProperties mqttProperties) {

            }
        });
    }
}
