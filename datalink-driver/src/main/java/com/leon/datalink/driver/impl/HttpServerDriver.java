package com.leon.datalink.driver.impl;

import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.listener.ListenerContent;
import com.leon.datalink.core.listener.ListenerTypeEnum;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.entity.DriverProperties;
import com.leon.datalink.driver.util.SimpleHttpServer;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpServerDriver extends AbstractDriver {

    SimpleHttpServer simpleHttpServer;

    @Override
    public void create(DriverModeEnum driverMode, DriverProperties properties) throws Exception {
        Integer port = properties.getInteger("port");
        String path = properties.getString("path");
        if (null == port) return;
        if (StringUtils.isEmpty(path)) return;


        simpleHttpServer = new SimpleHttpServer(port)
                .addAction(path, (req, res) -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("path", req.getPath());
                    result.put("method", req.getMethod());
                    result.put("headers", req.getHeaders());
                    result.put("body", req.getBody());
                    result.put("params", req.getParams());

                    // 响应体解析
                    String response = properties.getString("response");
                    if (!StringUtils.isEmpty(response)) {
                        String render = this.templateAnalysis(response, getVariable(null));
                        if (!StringUtils.isEmpty(render)) {
                            response = render;
                        }
                        res.write(response);
                    }

                    result.put("response", response);
                    result.put("driver", properties);
                    this.sendData(result);
                });

        simpleHttpServer.start();
        ListenerContent.add(port, ListenerTypeEnum.TCP, "HTTP server driver port");
    }

    @Override
    public void destroy(DriverModeEnum driverMode, DriverProperties properties) throws Exception {
        if (null != simpleHttpServer) simpleHttpServer.stop(0);
        Integer port = properties.getInteger("port");
        if (null == port) return;
        ListenerContent.remove(port);
    }

    @Override
    public boolean test(DriverProperties properties) {
        Integer port = properties.getInteger("port");
        if (null == port) return false;
        return NetUtil.isUsableLocalPort(port);
    }

    @Override
    public Object handleData(Object data, DriverProperties properties) throws Exception {
        throw new UnsupportedOperationException();
    }


}
