package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.util.mqtt.MqttClientFactory;
import com.leon.datalink.resource.util.mqtt.MqttClientConfig;
import com.leon.datalink.resource.util.mqtt.MqttPoolConfig;
import com.leon.datalink.resource.util.mqtt.MqttTemplate;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MqttDriver extends AbstractDriver {

    private MqttClient mqttClient;

    private MqttTemplate mqttTemplate;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("topic"))) throw new ValidateException();

        MqttClientConfig mqttClientConfig = new MqttClientConfig();
        mqttClientConfig.setHostUrl(properties.getString("url"));
        mqttClientConfig.setUsername(properties.getString("username"));
        mqttClientConfig.setPassword(properties.getString("password", ""));
        mqttClientConfig.setConnectionTimeout(properties.getInteger("connectionTimeout", 10));
        mqttClientConfig.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 30));
        MqttClientFactory mqttClientFactory = new MqttClientFactory(mqttClientConfig);

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttClient = mqttClientFactory.create();
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {
                    produceDataError(throwable.getMessage());
                    Loggers.DRIVER.error("mqtt driver connectionLost {}", throwable.getMessage());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("topic", s);
                    data.put("payload", new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
                    produceData(data);
                }
            });
            Integer qos = properties.getInteger("qos", 0);
            String topic = properties.getString("topic");
            boolean multiTopic = topic.contains(",");
            if (multiTopic) {
                String[] topicList = topic.split(",");
                int[] qosList = new int[topicList.length];
                Arrays.fill(qosList, qos);
                mqttClient.subscribe(topicList, qosList);
            } else {
                mqttClient.subscribe(topic, qos);
            }
        } else if (driverMode.equals(DriverModeEnum.DEST)) {
            MqttPoolConfig mqttPoolConfig = new MqttPoolConfig();
            mqttPoolConfig.setMaxTotal(properties.getInteger("maxTotal", 8));
            mqttPoolConfig.setMaxIdle(properties.getInteger("maxIdle", 8));
            mqttPoolConfig.setMinIdle(properties.getInteger("minIdle", 4));
            mqttTemplate = new MqttTemplate(mqttClientFactory, mqttPoolConfig);
        }
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttClient.disconnect();
            mqttClient.close();
        } else if (driverMode.equals(DriverModeEnum.DEST)) {
            mqttTemplate.close();
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
        String topic = properties.getString("topic");

        // topic模板解析
        String render = this.templateAnalysis(topic, variable);
        if (!StringUtils.isEmpty(render)) topic = render;


        Integer qos = properties.getInteger("qos", 0);
        Boolean retained = properties.getBoolean("retained", false);

        // 发布消息
        mqttTemplate.publish(topic, payload.getBytes(StandardCharsets.UTF_8), qos, retained);

        return payload;
    }

}
