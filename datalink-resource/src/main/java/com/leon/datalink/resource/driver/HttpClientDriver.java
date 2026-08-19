package com.leon.datalink.resource.driver;

import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpClientDriver extends AbstractDriver {

    private HttpComponentsClientHttpRequestFactory requestFactory;

    private RestTemplate restTemplate;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {

        this.requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(properties.getInteger("connectTimeout", 6000));
        requestFactory.setReadTimeout(properties.getInteger("readTimeout", 6000));

        this.restTemplate = new RestTemplate(this.requestFactory);
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        requestFactory.destroy();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        String url = properties.getString("url");
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
    public void scheduleTrigger(ConfigProperties properties) throws Exception {
        produceData(doRequest(null, properties));
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        return doRequest(data, properties);
    }


    private Map<String, Object> doRequest(Object data, ConfigProperties properties) {

        String url = properties.getString("url");
        String method = properties.getString("method");
        if (StringUtils.isEmpty(url)) return null;
        if (StringUtils.isEmpty(method)) return null;

        Map<String, Object> variable = getVariable(data);

        // 路径模板解析
        String path = properties.getString("path");
        if (!StringUtils.isEmpty(path)) {
            String render = this.templateAnalysis(path, variable);
            if (!StringUtils.isEmpty(render)) path = render;
        }

        url += path;

        // 请求体模板解析
        String body = properties.getString("body");
        if (!StringUtils.isEmpty(body)) {
            String render = this.templateAnalysis(body, variable);
            if (!StringUtils.isEmpty(render)) body = render;
        } else {
            if (null != data) {
                body = JacksonUtils.toJson(data);
            }
        }

        Map<String, String> headersMap = properties.getMap("headers");

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
        result.put("driver", properties);
        return result;
    }

}
