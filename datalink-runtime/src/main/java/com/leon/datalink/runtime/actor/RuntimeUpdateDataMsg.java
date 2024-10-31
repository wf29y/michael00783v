package com.leon.datalink.runtime.actor;

import cn.hutool.core.date.DateTime;

import java.util.Map;

public class RuntimeUpdateDataMsg {

    private Map data;

    private boolean success;

    private DateTime time;

    public RuntimeUpdateDataMsg(Map data, boolean success, DateTime time) {
        this.data = data;
        this.success = success;
        this.time = time;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }
}
