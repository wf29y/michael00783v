package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.listener.ListenerContent;
import com.leon.datalink.core.listener.ListenerTypeEnum;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.resource.util.http.SimpleHttpServer;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpServerDriver extends AbstractDriver {

    SimpleHttpServer simpleHttpServer;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        Integer port = properties.getInteger("port");
        String path = properties.getString("path");
        if (null == port) throw new ValidateException();
        if (StringUtils.isEmpty(path)) throw new ValidateException();


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
                    this.produceData(result);
                });

        simpleHttpServer.start();
        ListenerContent.add(port, ListenerTypeEnum.TCP, "HTTP server driver port");
    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (null != simpleHttpServer) simpleHttpServer.stop(0);
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

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        throw new UnsupportedOperationException();
    }


}
