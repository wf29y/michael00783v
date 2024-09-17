package com.leon.datalink.driver.impl;

import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.DriverDataCallback;
import com.leon.datalink.driver.DriverModeEnum;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class KafkaDriver extends AbstractDriver {

    private  KafkaConsumer<String, String> kafkaConsumer;

    private  KafkaProducer<String, String> kafkaProducer;

    public KafkaDriver(Map<String, Object> properties, DriverModeEnum driverMode) throws Exception {
        super(properties, driverMode);
    }

    public KafkaDriver(Map<String, Object> properties, DriverModeEnum driverMode, DriverDataCallback callback) throws Exception {
        super(properties, driverMode, callback);
    }

    private static ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void create() {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            Properties prop = new Properties();
            prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getStrProp("url"));
            prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            prop.put(ConsumerConfig.GROUP_ID_CONFIG, getStrProp("group"));
            this.kafkaConsumer = new KafkaConsumer<>(prop);
            kafkaConsumer.subscribe(Collections.singletonList(getStrProp("topic")));
            executor.submit(() -> {   //todo 异步处理后 在销毁时如何断开kafkaConsumer？
                while (true) {
                    final ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(1));
                    for (ConsumerRecord<String, String> record : records) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("topic", record.topic());
                        data.put("payload", record.value());
                        callback.onData(data);
                    }
                }
            });
        } else {
            Properties prop = new Properties();
            prop.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getStrProp("url"));
            prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
            this.kafkaProducer = new KafkaProducer<>(prop);
        }
    }

    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            this.kafkaConsumer.unsubscribe();
            this.kafkaConsumer.close();
        } else {
            this.kafkaProducer.close();
        }
    }

    @Override
    public void handleData(Map data) throws Exception {

        // 消息模板解析
        String template = getStrProp("template");
        String payload = data.toString();
        if (!StringUtils.isEmpty(template)) {
            String render = this.templateEngine.getTemplate(template).render(data);
            if (!StringUtils.isEmpty(render)) payload = render;
        }

        // topic模板解析
        String topic = getStrProp("topic");
        if (getBoolProp("dynamicTopic", false)) {
            String render = this.templateEngine.getTemplate(topic).render(data);
            if (!StringUtils.isEmpty(render)) topic = render;
        }

        // 发布消息
        kafkaProducer.send(new ProducerRecord<>(topic, payload));
    }

}
