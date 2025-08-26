package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import cn.hutool.core.net.NetUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import org.apache.http.HttpRequestFactory;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HttpClientDriver extends AbstractDriver {

    private HttpComponentsClientHttpRequestFactory requestFactory;

    private RestTemplate restTemplate;

    private ScheduledExecutorService executor;

    public HttpClientDriver(Map<String, Object> properties) {
        super(properties);
    }

    public HttpClientDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        super(properties, driverMode, ruleActorRef, ruleId);
    }

    @Override
    public void create() throws Exception {
        this.requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(getIntProp("connectTimeout",6000));
        requestFactory.setReadTimeout(getIntProp("readTimeout",6000));

        this.restTemplate = new RestTemplate(this.requestFactory);

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            if(null== getLongProp("initialDelay")) return;
            if(null== getLongProp("period")) return;
            if(StringUtils.isEmpty(getStrProp("timeUnit"))) return;

            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                Map<String, Object> result = doRequest(null);
                sendData(result);
            }, getLongProp("initialDelay"), getLongProp("period"), TimeUnit.valueOf(getStrProp("timeUnit")));
        }
    }

    @Override
    public void destroy() throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            executor.shutdown();
        }
        requestFactory.destroy();
    }

    @Override
    public boolean test() {
        String url = getStrProp("url");
        if (StringUtils.isEmpty(url)) return false;
        try {
            URL urlObj = new URL(url);
            InetSocketAddress inetSocketAddress = new InetSocketAddress(urlObj.getHost(), urlObj.getPort());
            return NetUtil.isOpen(inetSocketAddress, 6000);
        } catch (Exception e) {
            Loggers.DRIVER.error("driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data) throws Exception {
        return doRequest(data);
    }


    private Map<String, Object> doRequest(Object data) {

        String url = getStrProp("url");
        String method = getStrProp("method");
        if(StringUtils.isEmpty(url)) return null;
        if(StringUtils.isEmpty(method)) return null;

        Map<String, Object> variable = getVariable(data);

        // 路径模板解析
        String path = getStrProp("path");
        if (!StringUtils.isEmpty(path)) {
            String render = this.templateEngine.getTemplate(path).render(variable);
            if (!StringUtils.isEmpty(render)) path = render;
        }

        url += path;

        // 请求体模板解析
        String body = getStrProp("body");
        if (!StringUtils.isEmpty(body)) {
            String render = this.templateEngine.getTemplate(body).render(variable);
            if (!StringUtils.isEmpty(render)) body = render;
        } else {
            if (null != data) {
                body = JacksonUtils.toJson(data);
            }
        }

        Map<String, String> headersMap = getMapProp("headers");

        final String param = body;
        RequestCallback requestCallback = request -> {
            if (null != headersMap) request.getHeaders().setAll(headersMap);
            if (!StringUtils.isEmpty(param)) request.getBody().write(param.getBytes(StandardCharsets.UTF_8));
        };

        String response = restTemplate.execute(url, HttpMethod.valueOf(method), requestCallback, clientHttpResponse -> {
            InputStream in = clientHttpResponse.getBody();
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[4096];
            for (int n; (n = in.read(b)) != -1; ) {
                out.append(new String(b, 0, n));
            }
            return out.toString();
        });


        Map<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("param", param);
        result.put("response", response);

        return result;
    }

}
