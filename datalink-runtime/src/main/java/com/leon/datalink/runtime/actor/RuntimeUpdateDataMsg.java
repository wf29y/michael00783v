package com.leon.datalink.runtime.actor;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Map;

public class RuntimeUpdateDataMsg {

    private Map<String,Object> receiveData;

    private Map<String,Object> analysisData;

    private Object publishData;

    private Boolean analysisSuccess;

    private Boolean publishSuccess;

    private String message;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime time;

    public Map<String,Object> getReceiveData() {
        return receiveData;
    }

    public void setReceiveData(Map<String,Object> receiveData) {
        this.receiveData = receiveData;
    }

    public Map<String, Object> getAnalysisData() {
        return analysisData;
    }

    public void setAnalysisData(Map<String, Object> analysisData) {
        this.analysisData = analysisData;
    }

    public Object getPublishData() {
        return publishData;
    }

    public void setPublishData(Object publishData) {
        this.publishData = publishData;
    }

    public Boolean getAnalysisSuccess() {
        return analysisSuccess;
    }

    public void setAnalysisSuccess(Boolean analysisSuccess) {
        this.analysisSuccess = analysisSuccess;
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
