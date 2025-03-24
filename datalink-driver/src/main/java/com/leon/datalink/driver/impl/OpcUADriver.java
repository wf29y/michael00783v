package com.leon.datalink.driver.impl;

import akka.actor.ActorRef;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.AbstractDriver;
import com.leon.datalink.driver.constans.DriverModeEnum;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.enumerated.TimestampsToReturn;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class OpcUADriver extends AbstractDriver {

    private OpcUaClient opcUaClient;

    private ScheduledExecutorService executor;

    public OpcUADriver(Map<String, Object> properties) {
        super(properties);
    }

    public OpcUADriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        super(properties, driverMode, ruleActorRef, ruleId);
    }

    @Override
    public void create() throws Exception {
        opcUaClient = OpcUaClient.create(
                getStrProp("url"),
                endpoints -> endpoints.stream().findFirst(),
                configBuilder -> configBuilder.setIdentityProvider(new AnonymousProvider())
                        .setRequestTimeout(uint(5000))
                        .build()
        );
        opcUaClient.connect();

        List<Map<String, Object>> points = getListProp("points");

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                try {
                    for (Map<String, Object> point : points) {
                        Loggers.DRIVER.info("points read {}", point);
                        HashMap<String, Object> result = new HashMap<>();
                        result.put("point", point);
                        result.put("value", read(point));
                        sendData(result);
                    }
                } catch (Exception e) {
                    Loggers.DRIVER.error("{}", e.getMessage());
                }
            }, getLongProp("initialDelay"), getLongProp("period"), TimeUnit.valueOf(getStrProp("timeUnit")));
        }

    }

    public String read(Map<String, Object> point) {
        int namespace = Integer.parseInt((String) point.get("namespace"));
        String tag = (String) point.get("tag");
        NodeId nodeId = new NodeId(namespace, tag);
        CompletableFuture<String> value = new CompletableFuture<>();
        opcUaClient.readValue(0.0, TimestampsToReturn.Both, nodeId).thenAccept(dataValue -> {
            try {
                value.complete(dataValue.getValue().getValue().toString());
            } catch (Exception e) {
                Loggers.DRIVER.error("accept point(ns={};s={}) value error: {}", namespace, tag, e.getMessage());
            }
        });
        try {
            return value.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            Loggers.DRIVER.error("read point(ns={};s={}) value error: {}", namespace, tag, e.getMessage());
        }
        return null;
    }


    @Override
    public void destroy() throws Exception {
        executor.shutdown();
        opcUaClient.disconnect();
    }

    @Override
    public boolean test() {
        try {
            OpcUaClient opcUaClient = OpcUaClient.create(
                    getStrProp("url"),
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
    public Object handleData(Object data) throws Exception {
        return null;
    }
}
