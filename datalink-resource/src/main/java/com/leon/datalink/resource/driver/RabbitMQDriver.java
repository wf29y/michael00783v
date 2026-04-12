package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.core.config.ConfigProperties;
import com.rabbitmq.client.*;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RabbitMQDriver extends AbstractDriver {

    Connection connection;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getInteger("port"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("virtualHost"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("username"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("password"))) throw new ValidateException();

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(properties.getString("ip"));
        factory.setPort(properties.getInteger("port"));
        factory.setVirtualHost(properties.getString("virtualHost"));
        factory.setUsername(properties.getString("username"));
        factory.setPassword(properties.getString("password"));

        connection = factory.newConnection();
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            if (StringUtils.isEmpty(properties.getString("queue"))) throw new ValidateException();
            Channel channel = connection.createChannel();
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("consumerTag", consumerTag);
                    map.put("payload", new String(body));
                    map.put("driver", properties);
                    produceData(map);
                }
            };
            channel.basicConsume(properties.getString("queue"), true, consumer);
        }

    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        connection.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) return false;
        if (StringUtils.isEmpty(properties.getInteger("port"))) return false;
        if (StringUtils.isEmpty(properties.getString("virtualHost"))) return false;
        if (StringUtils.isEmpty(properties.getString("username"))) return false;
        if (StringUtils.isEmpty(properties.getString("password"))) return false;

        try {
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost(properties.getString("ip"));
            factory.setPort(properties.getInteger("port"));
            factory.setVirtualHost(properties.getString("virtualHost"));
            factory.setUsername(properties.getString("username"));
            factory.setPassword(properties.getString("password"));
            Connection connection = factory.newConnection();
            return connection.isOpen();
        } catch (Exception e) {
            Loggers.DRIVER.error("rabbitmq driver driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        String queue = properties.getString("queue");
        if (StringUtils.isEmpty(queue)) throw new ValidateException();

        String exchange = properties.getString("exchange");
        if (StringUtils.isEmpty(exchange)) throw new ValidateException();

        Map<String, Object> variable = getVariable(data);

        // queue模板解析
        String queueRender = this.templateAnalysis(queue, variable);
        if (!StringUtils.isEmpty(queueRender)) queue = queueRender;

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

        Channel channel = connection.createChannel();
        channel.basicPublish(exchange, queue, null, payload.getBytes());

        return payload;
    }

}
