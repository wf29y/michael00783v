package com.leon.datalink.driver.actor;

import java.util.Map;

public class DriverDataMsg{

    private Map data;

    public DriverDataMsg(Map data) {
        this.data = data;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
