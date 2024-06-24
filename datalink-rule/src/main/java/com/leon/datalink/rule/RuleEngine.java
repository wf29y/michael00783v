package com.leon.datalink.rule;

import cn.hutool.core.date.DateTime;
import cn.hutool.json.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverDataCallback;
import com.leon.datalink.driver.DriverModeEnum;
import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.constants.RuleAnalysisModeEnum;
import com.leon.datalink.rule.entity.Rule;
import com.leon.datalink.rule.entity.RuleDriver;
import com.leon.datalink.rule.entity.RuleRuntime;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuleEngine implements IRuleEngine {

    /**
     * 规则列表
     */
    private final ConcurrentHashMap<String, RuleRuntime> runtimeList = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, RuleDriver> ruleDriverList = new ConcurrentHashMap<>();

    @Override
    public RuleRuntime getRuntime(String ruleId) {
        return runtimeList.get(ruleId);
    }

    @Override
    public void start(Rule rule) throws Exception {

        long time1 = System.currentTimeMillis();

        // 创建目的资源
        List<Resource> destResourceList = rule.getDestResourceList();
        List<Driver> destDriverList = new ArrayList<>();
        for (Resource destResource : destResourceList) {
            Driver destDriver = getDriver(destResource.getResourceType().getDriver(), destResource.getProperties(), DriverModeEnum.DEST);
            destDriver.create();
            destDriverList.add(destDriver);
        }
        // 源资源
        Resource sourceResource = rule.getSourceResource();
        Driver southDriver = getDriver(sourceResource.getResourceType().getDriver(), sourceResource.getProperties(),
                DriverModeEnum.SOURCE,
                // 源数据处理
                data -> {

                    // 更新运行状态
                    RuleRuntime ruleRuntime = runtimeList.get(rule.getRuleId());
                    ruleRuntime.addLastData(data);
                    ruleRuntime.setLastTime(DateTime.now());

                    try {
                        Loggers.RULE.info("receive {} source data: {}", sourceResource.getResourceType(), data);

                        Object result = null;
                        RuleAnalysisModeEnum analysisMode = rule.getAnalysisMode();
                        switch (analysisMode) {
                            case WITHOUT: {
                                result = data;
                                break;
                            }
                            case SCRIPT: {
                                result = scriptHandler(rule.getScript(), data);
                                break;
                            }
                            case JAR: {
                                // todo jar包解析
                                break;
                            }
                        }
                        // 忽略空值
                        if (null == result && rule.isIgnoreNullValue()) return;

                        // 数据目标处理
                        for (Driver driver : destDriverList) {
                            driver.handleData(result);
                        }
                        ruleRuntime.addSuccess();
                    } catch (Exception e) {
                        ruleRuntime.addFail();
                        Loggers.RULE.info("error {}", e.getMessage());
                    }
                    runtimeList.put(rule.getRuleId(), ruleRuntime);
                });
        southDriver.create();

        // 保存驱动
        RuleDriver ruleDriver = new RuleDriver();
        ruleDriver.setSourceDriver(southDriver);
        ruleDriver.setDestDriverList(destDriverList);
        ruleDriverList.put(rule.getRuleId(), ruleDriver);

        // 初始化运行状态
        RuleRuntime ruleRuntime = new RuleRuntime();
        ruleRuntime.setSuccessCount(0L);
        ruleRuntime.setFailCount(0L);
        ruleRuntime.setStartTime(DateTime.now());
        ruleRuntime.setLastData(new LinkedList<>());
        runtimeList.put(rule.getRuleId(), ruleRuntime);

        long time2 = System.currentTimeMillis();
        Loggers.RULE.info("start rule success:{} total use:{}ms", rule.getRuleId(), time2 - time1);
    }

    // 脚本解析
    private Object scriptHandler(String script, Object data) {
        if (StringUtils.isEmpty(script) || null == data) {
            return null;
        }
        try {
            ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("javascript");
            scriptEngine.eval(script);
            Invocable jsInvoke = (Invocable) scriptEngine;
            Object transform = jsInvoke.invokeFunction("transform", data);
            return new ObjectMapper().convertValue(transform, Map.class);
        } catch (Exception e) {
            Loggers.RULE.error("script error {}", e.getMessage());
        }
        return null;
    }


    @Override
    public void stop(String ruleId) throws Exception {
        RuleDriver ruleDriver = ruleDriverList.get(ruleId);
        if (null == ruleDriver) return;
        Driver sourceDriver = ruleDriver.getSourceDriver();
        sourceDriver.destroy();
        List<Driver> destDriverList = ruleDriver.getDestDriverList();
        for (Driver driver : destDriverList) {
            driver.destroy();
        }
        ruleDriverList.remove(ruleId);
        runtimeList.remove(ruleId);
        Loggers.RULE.info("stop rule:{}", ruleId);
    }

    @PreDestroy
    public void stopAll() throws Exception {
        Enumeration<String> keys = ruleDriverList.keys();
        if (keys.hasMoreElements()) {
            String ruleId = keys.nextElement();
            this.stop(ruleId);
        }
    }

    private Driver getDriver(Class<? extends Driver> driverClass, Map<String, Object> properties, DriverModeEnum driverMode) throws Exception {
        if (driverClass == null) return null;
        Constructor<? extends Driver> constructor = driverClass.getDeclaredConstructor(Map.class, DriverModeEnum.class);
        return constructor.newInstance(properties, driverMode);
    }

    private Driver getDriver(Class<? extends Driver> driverClass, Map<String, Object> properties, DriverModeEnum driverMode, DriverDataCallback callback) throws Exception {
        if (driverClass == null) return null;
        Constructor<? extends Driver> constructor = driverClass.getDeclaredConstructor(Map.class, DriverModeEnum.class, DriverDataCallback.class);
        return constructor.newInstance(properties, driverMode, callback);
    }

}
