package com.leon.datalink.driver.impl;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import org.apache.pulsar.client.api.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class PulsarDriver extends AbstractDriver {

    PulsarClient client;

    Consumer<byte[]> consumer;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("topic"))) throw new ValidateException();

        ClientBuilder clientBuilder = PulsarClient.builder();
        clientBuilder.serviceUrl(properties.getString("url"));
        client = clientBuilder.build();

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            String topic = properties.getString("topic");
            String subscriptionName = properties.getString("subscriptionName");
            if (StringUtils.isEmpty(topic)) throw new ValidateException();
            if (StringUtils.isEmpty(subscriptionName)) throw new ValidateException();

            ConsumerBuilder<byte[]> consumerBuilder = client.newConsumer()
                    .subscriptionName(subscriptionName)
                    .subscriptionType(SubscriptionType.valueOf(properties.getString("subscriptionType","Exclusive")))
                    .subscriptionMode(SubscriptionMode.valueOf(properties.getString("subscriptionMode","Durable")))
                    .messageListener((con, msg) -> {
                        try {
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("payload", new String(msg.getData()));
                            map.put("driver", properties);
                            produceData(map);
                            con.acknowledge(msg);
                        } catch (Exception e) {
                            produceDataError(e.getMessage());
                            con.negativeAcknowledge(msg);
                        }
                    });
            if (properties.getBoolean("multiTopic",false)) {
                consumerBuilder.topicsPattern(topic);
            } else {
                consumerBuilder.topic(topic);
            }

            consumer = consumerBuilder.subscribe();
        }


    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            consumer.close();
        }
        client.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new ValidateException();

        PulsarClient client = null;
        try {
            ClientBuilder clientBuilder = PulsarClient.builder();
            clientBuilder.serviceUrl(properties.getString("url"));
            client = clientBuilder.build(); // todo test
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("pulsar driver test {}", e.getMessage());
            return false;
        } finally {
            if (null != client) {
                try {
                    client.close();
                } catch (Exception e) {
                    Loggers.DRIVER.error("pulsar driver test {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        String topic = properties.getString("topic");

        Map<String, Object> variable = getVariable(data);

        // topic模板解析
        String topicRender = this.templateAnalysis(topic, variable);
        if (!StringUtils.isEmpty(topicRender)) topic = topicRender;

        // 消息模板解析
        String payload = properties.getString("payload");
        if (!StringUtils.isEmpty(payload)) {
            String payloadRender = this.templateAnalysis(payload, variable);
            if (!StringUtils.isEmpty(payloadRender)) payload = payloadRender;
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        Producer<byte[]> producer = client.newProducer()
                .topic(topic)
                .create();
        producer.send(payload.getBytes());
        producer.close();

        return payload;
    }

}
