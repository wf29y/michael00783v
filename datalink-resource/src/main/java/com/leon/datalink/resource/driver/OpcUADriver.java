package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import com.leon.datalink.resource.util.opcua.OpcUAClientConfig;
import com.leon.datalink.resource.util.opcua.OpcUAClientFactory;
import com.leon.datalink.resource.util.opcua.OpcUAPoolConfig;
import com.leon.datalink.resource.util.opcua.OpcUATemplate;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpcUADriver extends AbstractDriver {

    private OpcUATemplate opcUATemplate;

    private final HashMap<String, NodeId> readPointMap = new HashMap<>();

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new ValidateException();

        OpcUAClientConfig opcUAClientConfig = new OpcUAClientConfig();
        opcUAClientConfig.setIp(properties.getString("ip"));
        opcUAClientConfig.setPort(properties.getInteger("port"));
        opcUAClientConfig.setUsername(properties.getString("username"));
        opcUAClientConfig.setPassword(properties.getString("password"));
        opcUAClientConfig.setConnectTimeout(properties.getInteger("connectTimeout", 5000));
        opcUAClientConfig.setRequestTimeout(properties.getInteger("requestTimeout", 60000));
        opcUAClientConfig.setKeepAliveInterval(properties.getInteger("keepAliveInterval", 5000));
        opcUAClientConfig.setKeepAliveTimeout(properties.getInteger("keepAliveTimeout", 5000));

        OpcUAPoolConfig opcUAPoolConfig = new OpcUAPoolConfig();
        opcUAPoolConfig.setMaxTotal(properties.getInteger("maxTotal", 8));
        opcUAPoolConfig.setMaxIdle(properties.getInteger("maxIdle", 8));
        opcUAPoolConfig.setMinIdle(properties.getInteger("minIdle", 4));

        opcUATemplate = new OpcUATemplate(new OpcUAClientFactory(opcUAClientConfig), opcUAPoolConfig);

        // 解析待读取点位
        if (DriverModeEnum.SOURCE.equals(driverMode)) {
            List<Map<String, Object>> points = properties.getList("points");
            if (null == points || points.isEmpty()) throw new ValidateException();
            points.forEach(point -> readPointMap.put((String) point.get("tag"), parse((String) point.get("address"))));
        }

    }

    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        opcUATemplate.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) return false;
        if (StringUtils.isEmpty(properties.getString("port"))) return false;
        try {
            OpcUAClientConfig opcUAClientConfig = new OpcUAClientConfig();
            opcUAClientConfig.setIp(properties.getString("ip"));
            opcUAClientConfig.setPort(properties.getInteger("port"));
            opcUAClientConfig.setUsername(properties.getString("username"));
            opcUAClientConfig.setPassword(properties.getString("password"));
            OpcUAClientFactory opcUAClientFactory = new OpcUAClientFactory(opcUAClientConfig);
            OpcUaClient opcUaClient = opcUAClientFactory.create();
            return opcUaClient.getSession().get() != null;
        } catch (Exception e) {
            Loggers.DRIVER.error("driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void scheduleTrigger(ConfigProperties properties) throws Exception {
        if (readPointMap.isEmpty()) return;
        try {
            Map<NodeId, DataValue> nodeIdObjectMap = opcUATemplate.readValues(new ArrayList<>(readPointMap.values()));
            List<HashMap<String, Object>> resultList = new ArrayList<>();
            readPointMap.forEach((tag, nodeId) -> {
                DataValue dataValue = nodeIdObjectMap.get(nodeId);
                HashMap<String, Object> resultItem = new HashMap<>();
                resultItem.put("tag", tag);
                resultItem.put("success", dataValue.getStatusCode() != null && dataValue.getStatusCode().isGood());
                resultItem.put("value", dataValue.getValue().getValue());
                resultItem.put("timestamp", dataValue.getServerTime() == null ? null : dataValue.getServerTime().getJavaTime());
                resultList.add(resultItem);
            });
            String transferType = properties.getString("transferType", "single");
            if("single".equals(transferType)){
                resultList.forEach(this::produceData);
            }else {
                produceData(resultList);
            }
        } catch (Exception e) {
            Loggers.DRIVER.error("opcua read error {}", e.getMessage());
        }
    }

    // 解析点位地址
    private NodeId parse(String address) {
        String[] elements = address.split(";");
        if (elements.length != 2) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }

        // 解析ns
        int namespaceIndex;
        String[] nsPair = elements[0].split("=");
        if (nsPair.length != 2) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }
        if (!nsPair[0].equals("ns")) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }
        namespaceIndex = Integer.parseInt(nsPair[1].trim());

        // 解析identifier
        String[] identifierPair = elements[1].split("=");
        if (identifierPair.length != 2) {
            throw new IllegalArgumentException("Invalid node identifier: " + address);
        }
        if (identifierPair[0].equals("i")) {
            return new NodeId(namespaceIndex, Integer.parseInt(identifierPair[1].trim()));
        } else {
            return new NodeId(namespaceIndex, identifierPair[1].trim());
        }

    }

}
