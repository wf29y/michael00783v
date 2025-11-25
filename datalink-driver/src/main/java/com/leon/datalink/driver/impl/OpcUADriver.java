package com.leon.datalink.driver.impl;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.driver.entity.DriverProperties;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class OpcUADriver extends AbstractDriver {

    private OpcUaClient opcUaClient;

    private ScheduledExecutorService executor;


    @Override
    public void create(DriverModeEnum driverMode, DriverProperties properties) throws Exception {
        String url = properties.getString("url");
        if (StringUtils.isEmpty(url)) throw new ValidateException();

        opcUaClient = OpcUaClient.create(
                url,
                endpoints -> endpoints.stream().findFirst(),
                configBuilder -> configBuilder.setIdentityProvider(new AnonymousProvider())
                        .setRequestTimeout(uint(5000))
                        .build()
        );
        opcUaClient.connect();

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            if (null == properties.getLong("initialDelay")) throw new ValidateException();
            if (null == properties.getLong("period")) throw new ValidateException();
            if (StringUtils.isEmpty(properties.getString("timeUnit"))) throw new ValidateException();

            List<Map<String, Object>> points = properties.getList("points");
            if (null == points || points.isEmpty()) throw new ValidateException();

            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                try {
                    for (Map<String, Object> point : points) {
                        //Loggers.DRIVER.info("points read {}", point);
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("point", point);
                        result.put("value", read(point));
                        result.put("url", url);
                        produceData(result);
                    }
                } catch (Exception e) {
                    produceDataError(e.getMessage());
                    Loggers.DRIVER.error("{}", e.getMessage());
                }
            }, properties.getLong("initialDelay"), properties.getLong("period"), TimeUnit.valueOf(properties.getString("timeUnit")));
        }

    }

    public String read(Map<String, Object> point) throws Exception {
        int namespace = Integer.parseInt((String) point.get("namespace"));
        String tag = (String) point.get("tag");
        NodeId nodeId = new NodeId(namespace, tag);
        CompletableFuture<String> value = new CompletableFuture<>();
        opcUaClient.readValue(0.0, TimestampsToReturn.Both, nodeId).thenAccept(dataValue -> {
            try {
                value.complete(dataValue.getValue().getValue().toString());
            } catch (Exception e) {
                Loggers.DRIVER.error("accept point(ns={};s={}) value error: {}", namespace, tag, e.getMessage());
                throw e;
            }
        });
        return value.get(1, TimeUnit.SECONDS);
    }


    @Override
    public void destroy(DriverModeEnum driverMode, DriverProperties properties) throws Exception {
        executor.shutdown();
        opcUaClient.disconnect();
    }

    @Override
    public boolean test(DriverProperties properties) {
        String url = properties.getString("url");
        if (StringUtils.isEmpty(url)) return false;

        try {
            OpcUaClient opcUaClient = OpcUaClient.create(
                    url,
                    endpoints -> endpoints.stream().findFirst(),
                    configBuilder -> configBuilder.setIdentityProvider(new AnonymousProvider())
                            .setRequestTimeout(uint(5000))
                            .build()
            );
            opcUaClient.connect();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Object handleData(Object data, DriverProperties properties) throws Exception {
        throw new UnsupportedOperationException();
    }
}
