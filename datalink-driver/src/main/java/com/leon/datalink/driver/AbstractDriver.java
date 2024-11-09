package com.leon.datalink.driver;


import akka.actor.ActorRef;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.leon.datalink.core.common.GlobalVariableContent;
import com.leon.datalink.driver.actor.DriverDataMsg;
import com.leon.datalink.driver.constans.DriverModeEnum;
import com.leon.datalink.runtime.RuntimeManger;

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
    public Map<String, Object> getVariable(Map<String, Object> data) {
        Map<String, Object> globalProp = GlobalVariableContent.get();
        Map<String, Object> ruleProp = RuntimeManger.getVariables(ruleId);
        if (null != ruleProp) {
            globalProp.putAll(ruleProp);
        }
        if (null != data) {
            globalProp.putAll(data);
        }
        return globalProp;
    }

    // 发送数据
    public void sendData(Map<String, Object> data) {
        ruleActorRef.tell(new DriverDataMsg(data), ActorRef.noSender());
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
            return Integer.valueOf((String) o);
        }
    }

    public Integer getIntProp(String key, int defaultValue) {
        Integer intProp = this.getIntProp(key);
        if (null == intProp) return defaultValue;
        else return intProp;
    }

    public Boolean getBoolProp(String key) {
        Object o = properties.get(key);
        if (null == o) return null;
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return Boolean.valueOf((String) o);
        }
    }

    public Boolean getBoolProp(String key, boolean defaultValue) {
        Boolean boolProp = this.getBoolProp(key);
        if (null == boolProp) return defaultValue;
        else return boolProp;
    }

}
