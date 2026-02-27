package com.leon.datalink.driver.impl;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.core.config.ConfigProperties;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


public class MqttDriver extends AbstractDriver {

    private volatile Map<Integer, MqttAsyncClient> mqttHandlerMap;

    private volatile MqttClient mqttHandler;

    private volatile Integer poolSize;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new ValidateException();

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttHandler = createClient(properties);
        } else {
            poolSize = properties.getInteger("poolSize", 10);
            mqttHandlerMap = new ConcurrentHashMap<>(poolSize);
            for (int i = 0; i < poolSize; i++) {
                mqttHandlerMap.put(i, createAsyncClient(properties));
            }
        }
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttHandler.disconnect();
            mqttHandler.close();
        } else {
            mqttHandlerMap.values().forEach(x -> {
                try {
                    x.disconnect();
                    x.close();
                } catch (MqttException e) {
                    Loggers.DRIVER.error("mqtt driver destroy {}",e.getMessage());
                }
            });
        }
    }

    @Override
    public boolean test(ConfigProperties properties) {
        String url = properties.getString("url");
        if (StringUtils.isEmpty(url)) return false;
        try {
            MqttClient mqttClient = new MqttClient(url, SnowflakeIdWorker.getId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(properties.getString("username"));
            options.setMaxInflight(1000);
            options.setPassword((properties.getString("password", "")).toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(30);
            mqttClient.connect(options);
            return true;
        } catch (MqttException e) {
            Loggers.DRIVER.error("mqtt driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        String topic = properties.getString("topic");
        if (StringUtils.isEmpty(topic)) throw new ValidateException();

        Map<String, Object> variable = getVariable(data);

        // 消息模板解析
        String payload = properties.getString("payload");
        if (!StringUtils.isEmpty(payload)) {
            String render = this.templateAnalysis(payload, variable);
            if (!StringUtils.isEmpty(render)) payload = render;
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        // topic模板解析
        String render = this.templateAnalysis(topic, variable);
        if (!StringUtils.isEmpty(render)) topic = render;


        Integer qos = properties.getInteger("qos", 0);
        Boolean retained = properties.getBoolean("retained", false);

        // 发布消息
        this.publish(topic, payload, qos, retained);

        return payload;
    }

    private void publish(String topic, String payload, int qos, boolean retained) throws MqttException {
        Random random = new Random();
        MqttAsyncClient messageHandler = mqttHandlerMap.get(random.nextInt(poolSize));
        if (messageHandler.isConnected()) {
            messageHandler.publish(topic, payload.getBytes(), qos, retained);
        } else {
            messageHandler.reconnect();
            this.publish(topic, payload, qos, retained);
        }
    }


    private MqttClient createClient(ConfigProperties properties) throws Exception {
        MqttClient mqttClient = new MqttClient(properties.getString("url"), SnowflakeIdWorker.getId(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        // 如果想要断线这段时间的数据，要设置成false，并且重连后不用再次订阅，否则不会得到断线时间的数据
        options.setCleanSession(true);
        // 设置连接的用户名
        options.setUserName(properties.getString("username"));
        // 增加 actualInFlight 的值
        options.setMaxInflight(1000);
        // 设置连接的密码
        options.setPassword(properties.getString("password", "").toCharArray());
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(properties.getInteger("connectionTimeout", 10));
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 30));
        // 自动重连
        options.setAutomaticReconnect(true);
        // 连接服务器
        mqttClient.connect(options);

        mqttClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
                try {
                    mqttClient.connect(options);
                } catch (MqttException e) {
                    Loggers.DRIVER.error("mqtt connection lost handle error {}", e.getMessage());
                }
            }

            @Override
            public void messageArrived(String s, MqttMessage mqttMessage) {
                Map<String, Object> data = new HashMap<>();
                data.put("topic", s);
                data.put("payload", mqttMessage.toString());
                data.put("driver", properties);
                produceData(data);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            }
        });
        String topic = properties.getString("topic");
        mqttClient.subscribe(topic);

        return mqttClient;
    }

    private MqttAsyncClient createAsyncClient(ConfigProperties properties) throws Exception {
        MqttAsyncClient mqttClient = new MqttAsyncClient(properties.getString("url"), SnowflakeIdWorker.getId(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        // 如果想要断线这段时间的数据，要设置成false，并且重连后不用再次订阅，否则不会得到断线时间的数据
        options.setCleanSession(true);
        // 设置连接的用户名
        options.setUserName(properties.getString("username"));
        // 增加 actualInFlight 的值
        options.setMaxInflight(1000);
        // 设置连接的密码
        options.setPassword(properties.getString("password", "").toCharArray());
        // 设置超时时间 单位为秒
        options.setConnectionTimeout(properties.getInteger("connectionTimeout", 10));
        // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
        options.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 30));
        // 自动重连
        options.setAutomaticReconnect(true);
        // 连接服务器
        mqttClient.connect(options);
        return mqttClient;
    }

}
