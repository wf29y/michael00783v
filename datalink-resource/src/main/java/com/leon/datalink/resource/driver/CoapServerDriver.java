package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.monitor.ListenerContent;
import com.leon.datalink.core.monitor.ListenerTypeEnum;
import com.leon.datalink.core.utils.HexUtil;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.network.Exchange;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CoapServerDriver extends AbstractDriver {

    CoapServer coapServer;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        Integer port = properties.getInteger("port");
        String path = properties.getString("path");
        if (null == port) throw new ValidateException();
        if (StringUtils.isEmpty(path)) throw new ValidateException();

        String payloadType = properties.getString("payloadType", "hex");

        coapServer = new CoapServer(port);

        coapServer.add(new CoapResource(path) {
            @Override
            public void handleRequest(Exchange exchange) {
                CoapExchange coapExchange = new CoapExchange(exchange);

                Map<String, Object> result = new HashMap<>();
                result.put("method", coapExchange.getRequestCode().name());
                result.put("options", coapExchange.getRequestOptions());
                result.put("payload",
                        "hex".equals(payloadType) ?
                                HexUtil.bytesToHex(coapExchange.getRequestPayload()) : coapExchange.getRequestText()
                );
                // 响应体解析
                String response = properties.getString("response");
                if (!StringUtils.isEmpty(response)) {
                    String render = templateAnalysis(response, getVariable(null));
                    if (!StringUtils.isEmpty(render)) {
                        response = render;
                    }
                    if ("hex".equals(payloadType)) {
                        coapExchange.respond(CoAP.ResponseCode.CONTENT, HexUtil.hexToBytes(response));
                    } else {
                        coapExchange.respond(CoAP.ResponseCode.CONTENT, response);
                    }
                }
                result.put("response", response);
                produceData(result);
            }
        });

        coapServer.start();
        ListenerContent.add(port, ListenerTypeEnum.UDP, "CoAP server driver port");
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (null != coapServer) coapServer.destroy();
        Integer port = properties.getInteger("port");
        if (null == port) throw new ValidateException();
        ListenerContent.remove(port);
    }

    @Override
    public boolean test(ConfigProperties properties) {
        Integer port = properties.getInteger("port");
        if (null == port) return false;
        return NetUtil.isUsableLocalPort(port);
    }


}
