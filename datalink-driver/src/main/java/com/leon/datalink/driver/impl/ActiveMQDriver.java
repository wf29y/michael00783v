package com.leon.datalink.driver.impl;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.util.StringUtils;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

public class ActiveMQDriver extends AbstractDriver {

    Connection connection;

    Session session;

    MessageConsumer consumer;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("model"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString(properties.getString("model")))) throw new ValidateException();

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                properties.getString("username", ActiveMQConnectionFactory.DEFAULT_USER),
                properties.getString("password", ActiveMQConnectionFactory.DEFAULT_PASSWORD),
                properties.getString("url"));
        connection = connectionFactory.createConnection();
        session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
        connection.start();

        String model = properties.getString("model");
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            Destination destination;
            if ("queue".equals(model)) {
                destination = session.createQueue(properties.getString("queue"));
            } else {
                destination = session.createTopic(properties.getString("topic"));
            }
            consumer = session.createConsumer(destination);
            consumer.setMessageListener(message -> {
                try {
                    TextMessage textMessage = (TextMessage) message;
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("payload", textMessage.getText());
                    map.put("driver", properties);
                    produceData(map);
                } catch (Exception e) {
                    produceDataError(e.getMessage());
                }
            });
        }
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            consumer.close();
        }
        session.close();
        connection.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("url"))) throw new ValidateException();
        Connection connection = null;
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(
                    properties.getString("username", ActiveMQConnectionFactory.DEFAULT_USER),
                    properties.getString("password", ActiveMQConnectionFactory.DEFAULT_PASSWORD),
                    properties.getString("url"));
            connection = connectionFactory.createConnection();
            connection.start();
            return true;
        } catch (Exception e) {
            Loggers.DRIVER.error("activemq driver test {}", e.getMessage());
            return false;
        } finally {
            if (null != connection) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    Loggers.DRIVER.error("activemq driver test {}", e.getMessage());
                }
            }
        }
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        String model = properties.getString("model"); // queue or topic
        String dest = properties.getString(model);

        Map<String, Object> variable = getVariable(data);

        // queue或topic模板解析
        String destRender = this.templateAnalysis(dest, variable);
        if (!StringUtils.isEmpty(destRender)) dest = destRender;

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

        Destination destination;
        if ("queue".equals(model)) {
            destination = session.createQueue(dest);
        } else {
            destination = session.createTopic(dest);
        }
        MessageProducer producer = session.createProducer(destination);
        TextMessage textMessage = session.createTextMessage(payload);
        producer.send(textMessage);
        producer.close();
        return payload;
    }

}
