package com.leon.datalink.rule;

import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.driver.Driver;
import com.leon.datalink.driver.DriverMessageCallback;
import com.leon.datalink.resource.Resource;
import org.springframework.stereotype.Service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RuleEngine implements IRuleEngine {

    /**
     * 规则列表
     */
    private final ConcurrentHashMap<String, RuleContent> ruleList = new ConcurrentHashMap<>();

    @Override
    public void start(Rule rule) throws Exception {

        // 目的资源
        List<Resource> destResourceList = rule.getDestResourceList();
        List<Driver> destDriverList = new ArrayList<>();
        for (Resource destResource : destResourceList) {
            Driver destDriver = getDriver(destResource.getResourceType().getDriver(), destResource.getProperties());
            destDriver.create();
            destDriverList.add(destDriver);
        }

        // 源资源
        Resource sourceResource = rule.getSourceResource();
        Driver southDriver = getDriver(sourceResource.getResourceType().getDriver(), sourceResource.getProperties(), message -> {
             Loggers.RULE.info("message: {}",message);
            // todo 脚本计算
            String script = rule.getScript();



            destDriverList.forEach(driver -> driver.handleMessage(message));
        });
        southDriver.create();

        RuleContent ruleContent = new RuleContent();
        ruleContent.setRule(rule);
        ruleContent.setSourceDriver(southDriver);
        ruleContent.setDestDriverList(destDriverList);
        ruleList.put(rule.getRuleId(), ruleContent);
    }

    @Override
    public void stop(Rule rule) throws Exception {
        RuleContent ruleContent = ruleList.get(rule.getRuleId());
        if (null == ruleContent) return;
        Driver sourceDriver = ruleContent.getSourceDriver();
        sourceDriver.destroy();
        List<Driver> destDriverList = ruleContent.getDestDriverList();
        for (Driver driver : destDriverList) {
            driver.destroy();
        }
        ruleList.remove(rule.getRuleId());
    }


    private Driver getDriver(Class<? extends Driver> driverClass, Map<String, Object> properties) throws Exception {
        if (driverClass == null) return null;
        Constructor<? extends Driver> constructor = driverClass.getDeclaredConstructor(Map.class);
        return constructor.newInstance(properties);
    }

    private Driver getDriver(Class<? extends Driver> driverClass, Map<String, Object> properties, DriverMessageCallback callback) throws Exception {
        if (driverClass == null) return null;
        Constructor<? extends Driver> constructor = driverClass.getDeclaredConstructor(Map.class, DriverMessageCallback.class);
        return constructor.newInstance(properties, callback);
    }

}
