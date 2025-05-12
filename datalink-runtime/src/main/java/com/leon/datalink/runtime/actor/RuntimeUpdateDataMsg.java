package com.leon.datalink.runtime.actor;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;

public class RuntimeUpdateDataMsg {

    private Object receiveData;

    private Object transformData;

    private Object publishData;

    private Boolean transformSuccess;

    private Boolean publishSuccess;

    private String message;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime time;

    public Object getReceiveData() {
        return receiveData;
    }

    public void setReceiveData(Object receiveData) {
        this.receiveData = receiveData;
    }

    public Object getTransformData() {
        return transformData;
    }

    public void setTransformData(Object transformData) {
        this.transformData = transformData;
    }

    public Boolean getTransformSuccess() {
        return transformSuccess;
    }

    public void setTransformSuccess(Boolean transformSuccess) {
        this.transformSuccess = transformSuccess;
    }

    public Object getPublishData() {
        return publishData;
    }

    public void setPublishData(Object publishData) {
        this.publishData = publishData;
    }

    public Boolean getPublishSuccess() {
        return publishSuccess;
    }

    public void setPublishSuccess(Boolean publishSuccess) {
        this.publishSuccess = publishSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }
}
