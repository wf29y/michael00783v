package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.rabbitmq.client.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RabbitMQDriver extends AbstractDriver {

    Connection connection;

    public RabbitMQDriver(Map<String, Object> properties) {
        super(properties);
    }

    public RabbitMQDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        super(properties, driverMode, ruleActorRef, ruleId);
    }

    @Override
    public void create() throws Exception {
        if (StringUtils.isEmpty(getStrProp("ip"))) return;
        if (StringUtils.isEmpty(getIntProp("port"))) return;
        if (StringUtils.isEmpty(getStrProp("virtualHost"))) return;
        if (StringUtils.isEmpty(getStrProp("username"))) return;
        if (StringUtils.isEmpty(getStrProp("password"))) return;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(getStrProp("ip"));
        factory.setPort(getIntProp("port"));
        factory.setVirtualHost(getStrProp("virtualHost"));
        factory.setUsername(getStrProp("username"));
        factory.setPassword(getStrProp("password"));
        try {
            connection = factory.newConnection();
            if (driverMode.equals(DriverModeEnum.SOURCE)) {
                if (StringUtils.isEmpty(getStrProp("queue"))) return;
                Channel channel = connection.createChannel();
                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("consumerTag", consumerTag);
                        map.put("payload", new String(body));
                        sendData(map);
                    }
                };
                channel.basicConsume(getStrProp("queue"), true, consumer);
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("rabbitmq driver create error {}", e.getMessage());
        }
    }

    @Override
    public void destroy() throws Exception {
        connection.close();
    }

    @Override
    public boolean test() {
        if (StringUtils.isEmpty(getStrProp("ip"))) return false;
        if (StringUtils.isEmpty(getIntProp("port"))) return false;
        if (StringUtils.isEmpty(getStrProp("virtualHost"))) return false;
        if (StringUtils.isEmpty(getStrProp("username"))) return false;
        if (StringUtils.isEmpty(getStrProp("password"))) return false;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(getStrProp("ip"));
            factory.setPort(getIntProp("port"));
            factory.setVirtualHost(getStrProp("virtualHost"));
            factory.setUsername(getStrProp("username"));
            factory.setPassword(getStrProp("password"));
            Connection connection = factory.newConnection();
            return connection.isOpen();
        } catch (Exception e) {
            Loggers.DRIVER.error("rabbitmq driver driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data) throws Exception {
        String queue = getStrProp("queue");
        if (StringUtils.isEmpty(queue)) return null;

        String exchange = getStrProp("exchange");
        if (StringUtils.isEmpty(exchange)) return null;

        Map<String, Object> variable = getVariable(data);

        // queue模板解析
        String queueRender = this.templateEngine.getTemplate(queue).render(variable);
        if (!StringUtils.isEmpty(queueRender)) queue = queueRender;

        // 消息模板解析
        String payload = getStrProp("payload");
        if (!StringUtils.isEmpty(payload)) {
            String payloadRender = this.templateEngine.getTemplate(payload).render(variable);
            if (!StringUtils.isEmpty(payloadRender)) payload = payloadRender;
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        Channel channel = connection.createChannel();
        channel.basicPublish(exchange, queue, null, payload.getBytes());

        return payload;
    }

}
