package com.leon.datalink.driver.actor;

import java.util.Map;

public class DriverDataMsg{

    private Map<String, Object> data;

    public DriverDataMsg(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
