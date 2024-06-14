package com.leon.datalink.driver.mqtt;

import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.utils.SnowflakeIdWorker;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.DriverDataCallback;
import com.leon.datalink.driver.DriverModeEnum;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassNameMQTTClient
 * @Description
 * @Author Solley
 * @Date2022/4/8 15:52
 * @Version V1.0
 **/
public class MqttDriver extends AbstractDriver {

    private volatile Map<Integer, MqttAsyncClient> mqttHandlerMap;

    private volatile MqttClient mqttHandler;

    private static final Integer HANDLER_COUNT = 10;

    //SpringEL解析器
    private final ExpressionParser parser = new SpelExpressionParser();

    public MqttDriver(Map<String, Object> properties, DriverModeEnum driverMode) throws Exception {
        super(properties, driverMode);
    }

    public MqttDriver(Map<String, Object> properties, DriverModeEnum driverMode, DriverDataCallback callback) throws Exception {
        super(properties, driverMode, callback);
    }

    @Override
    public void create() {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttHandler = createClient();
        } else {
            mqttHandlerMap = new ConcurrentHashMap<>();
            for (int i = 0; i < HANDLER_COUNT; i++) {
                mqttHandlerMap.put(i, createAsyncClient());
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            mqttHandler.disconnect();
            mqttHandler.close();
        } else {
            mqttHandlerMap.values().forEach(x -> {
                try {
                    x.disconnect();
                    x.close();
                } catch (MqttException e) {
                    Loggers.DRIVER.error(e.getMessage());
                }
            });
        }
    }

    @Override
    public void handleData(Object data) throws Exception {
        String template = getStrProp("template");
        String payload = data.toString();

        // 消息模板解析
        if (!StringUtils.isEmpty(template)) {
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable("data", data);
            Expression expression = parser.parseExpression(template);
            String result = expression.getValue(context, String.class);
            if (!StringUtils.isEmpty(result)) payload = result;
        }

        // topic模板解析
        String topic = getStrProp("topic");
        if (getBoolProp("dynamicTopic", false)) {
            EvaluationContext context = new StandardEvaluationContext();
            context.setVariable("data", data);
            Expression expression = parser.parseExpression(topic);
            String result = expression.getValue(context, String.class);
            if (!StringUtils.isEmpty(result)) topic = result;
        }

        Integer qos = getIntProp("qos");
        Boolean retained = getBoolProp("retained", false);

        // 发布消息
        Random random = new Random();
        MqttAsyncClient messageHandler = mqttHandlerMap.get(random.nextInt(HANDLER_COUNT));
        if (!messageHandler.isConnected()) {
            messageHandler.reconnect();
            if (messageHandler.isConnected()) {
                messageHandler.publish(topic, payload.getBytes(), qos, retained);
            }
        } else {
            messageHandler.publish(topic, payload.getBytes(), qos, retained);
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
            options.setPassword(getStrProp("password", "").toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(getIntProp("connectionTimeout", 10));
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(getIntProp("keepAliveInterval", 30));
            // 连接服务器
            mqttClient.connect(options);

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


                    MqttData mqttData = new MqttData();
                    mqttData.setTopic(s);
                    mqttData.setPayload(mqttMessage.toString());
                    callback.onData(mqttData);
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

            return mqttClient;
        } catch (MqttException e) {
            Loggers.DRIVER.error("ReceiveMqttDriver {}", e.getMessage());
        }
        return null;
    }

    private MqttAsyncClient createAsyncClient() {
        try {
            MqttAsyncClient mqttClient = new MqttAsyncClient(getStrProp("url"), SnowflakeIdWorker.getId(), new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            // 如果想要断线这段时间的数据，要设置成false，并且重连后不用再次订阅，否则不会得到断线时间的数据
            options.setCleanSession(true);
            // 设置连接的用户名
            options.setUserName(getStrProp("username"));
            // 增加 actualInFlight 的值
            options.setMaxInflight(1000);
            // 设置连接的密码
            options.setPassword(getStrProp("password", "").toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(getIntProp("connectionTimeout", 10));
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(getIntProp("keepAliveInterval", 30));
            // 连接服务器
            mqttClient.connect(options);
            return mqttClient;
        } catch (MqttException e) {
            Loggers.DRIVER.error("ReceiveMqttDriver {}", e.getMessage());
        }
        return null;
    }

}
