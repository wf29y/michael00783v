package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.resource.util.mqtt.MqttClientConfig;
import com.leon.datalink.resource.util.mqtt.MqttClientFactory;
import com.leon.datalink.resource.util.mqtt.MqttPoolConfig;
import com.leon.datalink.resource.util.mqtt.MqttTemplate;
import com.leon.datalink.resource.util.mqtt.client.IMqttCallback;
import com.leon.datalink.resource.util.mqtt.client.IMqttClient;
import com.leon.datalink.resource.util.mqtt.client.MqttSubParam;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class MqttDriver extends AbstractDriver {

    private IMqttClient mqttClient;

    private MqttTemplate mqttTemplate;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("topic"))) throw new ValidateException();

        Integer version = properties.getInteger("version", 3);

        MqttClientConfig mqttClientConfig = new MqttClientConfig();
        mqttClientConfig.setHostUrl(properties.getString("url"));
        mqttClientConfig.setUsername(properties.getString("username"));
        mqttClientConfig.setPassword(properties.getString("password", ""));
        mqttClientConfig.setConnectionTimeout(properties.getInteger("connectionTimeout", 10));
        mqttClientConfig.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 30));
        mqttClientConfig.setAutoReconnect(properties.getBoolean("autoReconnect", true));
        mqttClientConfig.setSsl(properties.getBoolean("ssl", false));
        mqttClientConfig.setMqttVersion(version);
        MqttClientFactory mqttClientFactory = new MqttClientFactory(mqttClientConfig);

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttClient = mqttClientFactory.create();
            mqttClient.setCallback(new IMqttCallback() {
                @Override
                public void exceptionOccurred(String exceptionMessage) {
                    produceDataError(exceptionMessage);
                    Loggers.DRIVER.error("mqtt driver connectionLost {}", exceptionMessage);
                }

                @Override
                public void messageArrived(String topic, byte[] payload, int qos, boolean retained, Map<String, String> userProperties) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("topic", topic);
                    data.put("qos", qos);
                    data.put("retained", retained);
                    data.put("payload", new String(payload, StandardCharsets.UTF_8));
                    if (userProperties != null) data.put("userProperties", userProperties);
                    produceData(data);
                }
            });
            Integer qos = properties.getInteger("qos", 0);
            String topic = properties.getString("topic");
            Boolean noLocal = properties.getBoolean("noLocal", false);
            Boolean retainAsPublished = properties.getBoolean("retainAsPublished", false);
            Integer retainHandling = properties.getInteger("retainHandling", 0);
            MqttSubParam[] mqttSubParams = Arrays.stream(topic.split(",")).map(subTopic -> {
                if (version == 5) {
                    return new MqttSubParam(subTopic, qos, noLocal, retainAsPublished, retainHandling);
                } else {
                    return new MqttSubParam(subTopic, qos);
                }
            }).toArray(MqttSubParam[]::new);
            mqttClient.subscribe(mqttSubParams);

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
            MqttClientConfig mqttClientConfig = new MqttClientConfig();
            mqttClientConfig.setHostUrl(properties.getString("url"));
            mqttClientConfig.setUsername(properties.getString("username"));
            mqttClientConfig.setPassword(properties.getString("password", ""));
            mqttClientConfig.setConnectionTimeout(properties.getInteger("connectionTimeout", 10));
            mqttClientConfig.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 30));
            mqttClientConfig.setAutoReconnect(properties.getBoolean("autoReconnect", true));
            mqttClientConfig.setSsl(properties.getBoolean("ssl", false));
            mqttClientConfig.setMqttVersion(properties.getInteger("version", 3));
            MqttClientFactory mqttClientFactory = new MqttClientFactory(mqttClientConfig);
            IMqttClient mqttClient = mqttClientFactory.create();
            return mqttClient.isConnected();
        } catch (Exception e) {
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
        Map<String, String> userProperties = properties.getMap("userProperties");

        // 发布消息
        mqttTemplate.publish(topic, payload.getBytes(StandardCharsets.UTF_8), qos, retained, userProperties);

        return payload;
    }

}
