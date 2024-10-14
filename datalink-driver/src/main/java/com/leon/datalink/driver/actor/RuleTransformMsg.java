package com.leon.datalink.driver.actor;

import java.util.Map;

public class RuleTransformMsg {

    private Map data;

    public RuleTransformMsg(Map data) {
        this.data = data;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }
}
