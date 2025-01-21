package com.leon.datalink.driver.actor;


public class ReceiveDataMsg {

    private Object data;

    public ReceiveDataMsg(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
