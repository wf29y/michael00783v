package com.leon.datalink.driver;


import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

import java.util.Map;


public abstract class AbstractDriver implements Driver {

    protected Map<String, Object> properties;

    protected DriverDataCallback callback;

    protected DriverModeEnum driverMode;

    protected TemplateEngine templateEngine;

    public AbstractDriver(){
    }

    public AbstractDriver(Map<String, Object> properties){
        this.properties = properties;
    }

    public AbstractDriver(Map<String, Object> properties, DriverModeEnum driverMode) throws Exception {
        this.properties = properties;
        this.driverMode = driverMode;
        this.templateEngine = TemplateUtil.createEngine();
    }

    public AbstractDriver(Map<String, Object> properties, DriverModeEnum driverMode, DriverDataCallback callback) throws Exception {
        this.properties = properties;
        this.driverMode = driverMode;
        this.callback = callback;
        this.templateEngine = TemplateUtil.createEngine();
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
