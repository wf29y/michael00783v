package com.leon.datalink.driver.impl;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class RocketMQDriver extends AbstractDriver {

    DefaultMQProducer producer;

    DefaultMQPushConsumer consumer;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("group"))) throw new ValidateException();

        if (driverMode.equals(DriverModeEnum.DEST)) {
            // 生产者
            producer = new DefaultMQProducer(properties.getString("group"));
            producer.setNamesrvAddr(properties.getString("url"));
            producer.start();
        } else if (driverMode.equals(DriverModeEnum.SOURCE)) {
            //消费者
            if (StringUtils.isEmpty(properties.getString("topic"))) throw new ValidateException();

            consumer = new DefaultMQPushConsumer(properties.getString("group"));
            consumer.setNamesrvAddr(properties.getString("url"));
            consumer.subscribe(properties.getString("topic"), properties.getString("tags", "*"));
            consumer.setMessageModel(MessageModel.valueOf(properties.getString("model", "CLUSTERING")));

            consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> {
                for (MessageExt msg : list) {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("topic", msg.getTopic());
                    map.put("payload", new String(msg.getBody()));
                    map.put("driver", properties);
                    produceData(map);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            consumer.start();
        }
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (driverMode.equals(DriverModeEnum.DEST)) {
            producer.shutdown();
        } else if (driverMode.equals(DriverModeEnum.SOURCE)) {
            consumer.shutdown();
        }
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("url"))) return false;
        if (StringUtils.isEmpty(properties.getString("group"))) return false;

        DefaultMQPushConsumer consumer = null;
        try {
            consumer = new DefaultMQPushConsumer(properties.getString("group"));
            consumer.setNamesrvAddr(properties.getString("url"));
            consumer.registerMessageListener((MessageListenerConcurrently) (list, consumeConcurrentlyContext) -> ConsumeConcurrentlyStatus.CONSUME_SUCCESS);
            consumer.start();
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("rocketmq driver test {}", e.getMessage());
            return false;
        } finally {
            if (null != consumer) {
                consumer.shutdown();
            }
        }
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        String topic = properties.getString("topic");
        if (StringUtils.isEmpty(topic)) throw new ValidateException();

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

        String tags = properties.getString("tags", "");
        Message msg = new Message(topic, tags, payload.getBytes());

        //发送消息
        return producer.send(msg, properties.getInteger("timeout", 10000));
    }

}
