package com.leon.datalink.driver;


import akka.actor.ActorRef;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.leon.datalink.core.utils.JacksonUtils;
import com.leon.datalink.core.utils.Loggers;
import com.leon.datalink.core.variable.GlobalVariableContent;
import com.leon.datalink.driver.actor.ReceiveDataMsg;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.runtime.RuntimeManger;

import java.util.List;
import java.util.Map;


public abstract class AbstractDriver implements Driver {

    protected Map<String, Object> properties;

    protected ActorRef ruleActorRef;

    protected DriverModeEnum driverMode;

    protected TemplateEngine templateEngine;

    protected String ruleId;

    public AbstractDriver(Map<String, Object> properties) {
        this.properties = properties;
    }

    public AbstractDriver(Map<String, Object> properties, DriverModeEnum driverMode, ActorRef ruleActorRef, String ruleId) throws Exception {
        this.properties = properties;
        this.driverMode = driverMode;
        this.ruleActorRef = ruleActorRef;
        this.ruleId = ruleId;
        this.templateEngine = TemplateUtil.createEngine();
    }

    // 获得全局变量
    public Map<String, Object> getVariable(Object data) {
        Map<String, Object> globalVar = GlobalVariableContent.getAllValue();
        Map<String, Object> ruleVar = RuntimeManger.getVariables(ruleId);
        if (null != ruleVar) {
            globalVar.putAll(ruleVar);
        }
        if (JacksonUtils.canToMap(data)) {
            try {
                globalVar.putAll(JacksonUtils.convertValue(data, new TypeReference<Map<String, Object>>() {
                }));
            } catch (Exception e) {
                Loggers.DRIVER.error("data to map error {}", e.getMessage());
            }
        }
        return globalVar;
    }


    // 发送数据
    public void sendData(Object data) {
        ruleActorRef.tell(new ReceiveDataMsg(data), ActorRef.noSender());
    }

    public String getStrProp(String key) {
        Object o = properties.get(key);
        if (null != o) return (String) o;
        else return null;
    }

    public String getStrProp(String key, String defaultValue) {
        String strProp = this.getStrProp(key);
        if (null == strProp) return defaultValue;
        else return strProp;
    }

    public Integer getIntProp(String key) {
        Object o = properties.get(key);
        if (null == o) return null;
        if (o instanceof Integer) {
            return (Integer) o;
        } else {
            return JacksonUtils.convertValue(o, Integer.class);
        }
    }

    public Integer getIntProp(String key, int defaultValue) {
        Integer intProp = this.getIntProp(key);
        if (null == intProp) return defaultValue;
        else return intProp;
    }


    public Long getLongProp(String key) {
        Object o = properties.get(key);
        if (null == o) return null;
        if (o instanceof Long) {
            return (Long) o;
        } else {
            return JacksonUtils.convertValue(o, Long.class);
        }
    }

    public Long getLongProp(String key, long defaultValue) {
        Long longProp = this.getLongProp(key);
        if (null == longProp) return defaultValue;
        else return longProp;
    }

    public Boolean getBoolProp(String key) {
        Object o = properties.get(key);
        if (null == o) return null;
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return JacksonUtils.convertValue(o, Boolean.class);
        }
    }

    public Boolean getBoolProp(String key, boolean defaultValue) {
        Boolean boolProp = this.getBoolProp(key);
        if (null == boolProp) return defaultValue;
        else return boolProp;
    }


    public Map<String, String> getMapProp(String key) {
        Object o = properties.get(key);
        if (null == o) return null;
        return JacksonUtils.convertValue(o, new TypeReference<Map<String, String>>() {
        });
    }


    public List<Map<String, Object>> getListProp(String key) {
        Object o = properties.get(key);
        if (null == o) return null;
        return JacksonUtils.convertValue(o, new TypeReference<List<Map<String, Object>>>() {
        });
    }

}
