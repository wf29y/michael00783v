package com.leon.datalink.driver;


import java.util.Map;

/**
 * @ClassName AbstractDriver
 * @Description
 * @Author Solley
 * @Date 2022/4/8 15:52
 * @Version V1.0
 **/
public abstract class AbstractDriver implements Driver {

    protected Map<String, Object> properties;

    protected DriverMessageCallback callback;

    public AbstractDriver(Map<String, Object> properties) throws Exception {
        this.properties = properties;
    }

    public AbstractDriver(Map<String, Object> properties,DriverMessageCallback callback) throws Exception {
        this.properties = properties;
        this.callback = callback;
    }

    public String getStrProp(String key) {
        Object o = properties.get(key);
        if (null != o) return (String) o;
        else return null;
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

    public Boolean getBoolProp(String key) {
        Object o = properties.get(key);
        if (null == o) return null;
        if (o instanceof Boolean) {
            return (Boolean) o;
        } else {
            return Boolean.valueOf((String) o);
        }
    }

}
