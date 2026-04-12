package com.leon.datalink.resource.driver;

import cn.hutool.core.exceptions.ValidateException;
import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.resource.AbstractDriver;
import com.leon.datalink.resource.constans.DriverModeEnum;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class OpcUADriver extends AbstractDriver {

    private PlcConnection plcConnection;

    private ScheduledExecutorService executor;

    @Override
    public void create(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (StringUtils.isEmpty(properties.getString("ip"))) throw new ValidateException();
        if (StringUtils.isEmpty(properties.getString("port"))) throw new ValidateException();
        String connectionString;
        if (!StringUtils.isEmpty(properties.getString("username")) && !StringUtils.isEmpty(properties.getString("password"))) {
            connectionString = String.format("opcua:tcp://%s:%s?username=%s&password=%s", properties.getString("ip"), properties.getString("port"),
                    properties.getString("username"),
                    properties.getString("password"));
        } else {
            connectionString = String.format("opcua:tcp://%s:%s", properties.getString("ip"), properties.getString("port"));
        }
        plcConnection = new PlcDriverManager().getConnection(connectionString);

        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            if (null == properties.getLong("initialDelay")) throw new ValidateException();
            if (null == properties.getLong("period")) throw new ValidateException();
            if (StringUtils.isEmpty(properties.getString("timeUnit"))) throw new ValidateException();

            List<Map<String, Object>> points = properties.getList("points");
            if (null == points || points.isEmpty()) throw new ValidateException();

            this.executor = Executors.newSingleThreadScheduledExecutor();
            executor.scheduleAtFixedRate(() -> {
                try {
                    read(points);
                } catch (Exception e) {
                    produceDataError(e.getMessage());
                    Loggers.DRIVER.error("{}", e.getMessage());
                }
            }, properties.getLong("initialDelay"), properties.getLong("period"), TimeUnit.valueOf(properties.getString("timeUnit")));
        }

    }


    private void read(List<Map<String, Object>> points) throws ExecutionException, InterruptedException {
        PlcReadRequest.Builder builder = plcConnection.readRequestBuilder();

        points.forEach(point -> builder.addItem((String) point.get("tag"), (String) point.get("address")));
        PlcReadRequest readRequest = builder.build();

        PlcReadResponse response = readRequest.execute().get();
        for (String tag : response.getFieldNames()) {
            if (response.getResponseCode(tag) == PlcResponseCode.OK) {
                Collection<Object> allValues = response.getAllObjects(tag);
                allValues.forEach(value -> {
                    HashMap<String, Object> result = new HashMap<>();
                    result.put("tag", tag);
                    result.put("value", value);
                    produceData(result);
                });
            } else {
                Loggers.DRIVER.error("modbus read {} error: {}", tag, response.getResponseCode(tag).name());
            }
        }
    }


    @Override
    public void destroy(DriverModeEnum driverMode, ConfigProperties properties) throws Exception {
        if (driverMode.equals(DriverModeEnum.SOURCE)) {
            executor.shutdown();
        }
        plcConnection.close();
    }

    @Override
    public boolean test(ConfigProperties properties) {
        if (StringUtils.isEmpty(properties.getString("ip"))) return false;
        if (StringUtils.isEmpty(properties.getString("port"))) return false;

        try {
            String connectionString;
            if (!StringUtils.isEmpty(properties.getString("username")) && !StringUtils.isEmpty(properties.getString("password"))) {
                connectionString = String.format("opcua:tcp://%s:%s?username=%s&password=%s", properties.getString("ip"), properties.getString("port"),
                        properties.getString("username"),
                        properties.getString("password"));
            } else {
                connectionString = String.format("opcua:tcp://%s:%s", properties.getString("ip"), properties.getString("port"));
            }
            PlcConnection plcConnection = new PlcDriverManager().getConnection(connectionString);
            return plcConnection.isConnected();
        } catch (PlcConnectionException e) {
            Loggers.DRIVER.error("driver test {}", e.getMessage());
            return false;
        }
    }

    @Override
    public Object handleData(Object data, ConfigProperties properties) throws Exception {
        throw new UnsupportedOperationException();
    }
}
