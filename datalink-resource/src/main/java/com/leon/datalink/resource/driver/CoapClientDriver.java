package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.HexUtil;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CoapClientDriver extends AbstractDriver {

    CoapClient coapClient;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        String ip = properties.getString("ip");
        Integer port = properties.getInteger("port");
        String method = properties.getString("method");
        if (StringUtils.isEmpty(ip)) throw new ValidateException();
        if (null == port) throw new ValidateException();
        if (StringUtils.isEmpty(method)) throw new ValidateException();
        coapClient = new CoapClient(ip + ":" + port);
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (null != coapClient) {
            coapClient.shutdown();
        }
    }

    @Override
    public boolean test(ConfigProperties properties) {
        String ip = properties.getString("ip");
        Integer port = properties.getInteger("port");
        if (StringUtils.isEmpty(ip)) return false;
        if (null == port) return false;
        try {
            return NetUtil.isValidPort(port);
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


    private Map<String, Object> doRequest(Object data, ConfigProperties properties) throws ConnectorException, IOException {
        String payloadType = properties.getString("payloadType", "hex");

        Map<String, Object> variable = getVariable(data);

        // 路径模板解析
        String path = properties.getString("path");
        if (!StringUtils.isEmpty(path)) {
            String render = this.templateAnalysis(path, variable);
            if (!StringUtils.isEmpty(render)) path = render;
        }
        String url = String.format("%s:%s/%s", properties.getString("ip"), properties.getInteger("port"), path);
        coapClient.setURI(url);

        // 请求体模板解析
        String payload = properties.getString("payload");
        if (!StringUtils.isEmpty(payload)) {
            String render = this.templateAnalysis(payload, variable);
            if (!StringUtils.isEmpty(render)) payload = render;
        } else {
            if (null != data) {
                payload = JacksonUtils.toJson(data);
            }
        }

        Request request = new Request(CoAP.Code.valueOf(properties.getString("method")));
        request.setType(CoAP.Type.CON);
        request.getOptions().setContentFormat(MediaTypeRegistry.TEXT_PLAIN); //todo
        if ("hex".equals(payloadType)) {
            request.setPayload(HexUtil.hexToBytes(payload));
        } else {
            request.setPayload(payload);
        }

        CoapResponse response = coapClient.advanced(request);
        Map<String, Object> result = new HashMap<>();
        result.put("method", response.getCode().name());
        result.put("options", response.getOptions());
        result.put("payload", payload);
        if ("hex".equals(payloadType)) {
            result.put("response", HexUtil.bytesToHex(response.getPayload()));
        } else {
            result.put("response", response.getResponseText());
        }
        return result;
    }

}
