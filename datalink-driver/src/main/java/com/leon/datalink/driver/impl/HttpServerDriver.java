package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import cn.hutool.core.net.NetUtil;
import com.leon.datalink.core.listener.ListenerContent;
import com.leon.datalink.core.listener.ListenerTypeEnum;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.util.SimpleHttpServer;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HttpServerDriver extends AbstractDriver {

    SimpleHttpServer simpleHttpServer;

    public HttpServerDriver(Map<String, Object> properties) {
        super(properties);
    }

    public HttpServerDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        super(properties, driverMode, ruleActorRef, ruleId);
    }

    @Override
    public void create() throws Exception {
        Integer port = getIntProp("port");
        String path = getStrProp("path");
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
                    String response = getStrProp("response");
                    if (!StringUtils.isEmpty(response)) {
                        String render = this.templateEngine.getTemplate(response).render(getVariable(null));
                        if (!StringUtils.isEmpty(render)) {
                            response = render;
                        }
                        res.write(response);
                    }

                    result.put("response", response);
                    this.sendData(result);
                });

        simpleHttpServer.start();
        ListenerContent.add(port, ListenerTypeEnum.TCP, "HTTP Server Driver Port");
    }

    @Override
    public void destroy() throws Exception {
        if (null != simpleHttpServer) simpleHttpServer.stop(0);
        Integer port = getIntProp("port");
        if (null == port) return;
        ListenerContent.remove(port);
    }

    @Override
    public boolean test() {
        Integer port = getIntProp("port");
        if (null == port) return false;
        return NetUtil.isUsableLocalPort(port);
    }

    @Override
    public Object handleData(Object data) throws Exception {
        throw new UnsupportedOperationException();
    }


}
