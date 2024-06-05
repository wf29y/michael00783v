package com.leon.datalink.driver.mqtt;

import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverMessageCallback;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassNameMQTTClient
 * @Description
 * @Author Solley
 * @Date2022/4/8 15:52
 * @Version V1.0
 **/
public class MqttDriver extends AbstractDriver {

    private volatile Map<Integer, MqttClient> mqttHandlerMap;

    private static Integer HANDLER_COUNT;

    public MqttDriver(Map<String, Object> properties) throws Exception {
        super(properties);
        HANDLER_COUNT = 10;
    }


    public MqttDriver(Map<String, Object> properties, DriverMessageCallback callback) throws Exception {
        super(properties, callback);
        HANDLER_COUNT = 1;
    }

    @Override
    public void create() {
        mqttHandlerMap = new ConcurrentHashMap<>();
        for (int i = 0; i < HANDLER_COUNT; i++) {
            mqttHandlerMap.put(i, createClient());
        }
    }

    @Override
    public void destroy() throws Exception {
        for (MqttClient mqttClient : mqttHandlerMap.values()) {
            mqttClient.disconnect();
            mqttClient.close();
        }
        mqttHandlerMap.values().forEach(x -> {
            try {
                x.close();
            } catch (MqttException e) {
                Loggers.DRIVER.error(e.getMessage());
            }
        });
    }

    @Override
    public void handleMessage(Object message) {
        String data = message.toString();
        String topic = getStrProp("topic");
        Integer qos = getIntProp("qos");
        Boolean retained = getBoolProp("retained");
        if(retained==null) retained = false;
        Random random = new Random();
        MqttClient messageHandler = mqttHandlerMap.get(random.nextInt(HANDLER_COUNT));
        try {
            if (!messageHandler.isConnected()) {
                messageHandler.reconnect();
                if (messageHandler.isConnected()) {
                    messageHandler.publish(topic, data.getBytes(), qos, retained);
                }
            } else {
                messageHandler.publish(topic, data.getBytes(), qos, retained);
            }

        } catch (MqttException e) {
            Loggers.DRIVER.error(e.getMessage());
        }
    }


    private MqttClient createClient() {
        try {
            MqttClient mqttClient = new MqttClient(getStrProp("url"), SnowflakeIdWorker.getId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            // 如果想要断线这段时间的数据，要设置成false，并且重连后不用再次订阅，否则不会得到断线时间的数据
            options.setCleanSession(true);
            // 设置连接的用户名
            options.setUserName(getStrProp("username"));
            // 增加 actualInFlight 的值
            options.setMaxInflight(1000);
            // 设置连接的密码
            options.setPassword(StringUtils.isEmpty(getStrProp("password")) ? "".toCharArray() : getStrProp("password").toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(getIntProp("connectionTimeout") == null ? 10 : getIntProp("connectionTimeout"));
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(getIntProp("keepAliveInterval") == null ? 30 : getIntProp("keepAliveInterval"));
            // 连接服务器
            mqttClient.connect(options);

            if (HANDLER_COUNT == 1) {
                mqttClient.setCallback(new MqttCallback() {
                    @Override
                    public void connectionLost(Throwable throwable) {
                        try {
                            mqttClient.connect();
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) {
                        callback.onMessage(mqttMessage);
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                    }
                });
                try {
                    String topic = getStrProp("topic");
                    mqttClient.subscribe(topic);
                } catch (MqttException e) {
                    Loggers.DRIVER.error("ReceiveMqttDriver subscribe {}", e.getMessage());
                }
            }

            return mqttClient;
        } catch (MqttException e) {
            Loggers.DRIVER.error("ReceiveMqttDriver {}", e.getMessage());
        }
        return null;
    }
}
