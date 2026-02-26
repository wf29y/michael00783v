package com.leon.datalink.runtime.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;

import java.io.Serializable;

public class RuntimeData implements Serializable {
    private static final long serialVersionUID = 1895166087085804888L;

    private RuntimeTypeEnum type;

    private String entityRuntimeId;

    private Object data;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime time;

    private boolean error;

    private String errorMessage;

    public RuntimeData() {
    }

    public RuntimeData(RuntimeTypeEnum type) {
        this.type = type;
    }

    public RuntimeData(RuntimeTypeEnum type,String entityRuntimeId) {
        this.type = type;
        this.entityRuntimeId = entityRuntimeId;
    }

    public RuntimeData success(Object data) {
        this.data = data;
        this.time = DateTime.now();
        this.error = false;
        return this;
    }

    public RuntimeData fail(String errorMessage) {
        this.errorMessage = errorMessage;
        this.time = DateTime.now();
        this.error = true;
        return this;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public RuntimeTypeEnum getType() {
        return type;
    }

    public void setType(RuntimeTypeEnum type) {
        this.type = type;
    }

    public String getEntityRuntimeId() {
        return entityRuntimeId;
    }

    public void setEntityRuntimeId(String entityRuntimeId) {
        this.entityRuntimeId = entityRuntimeId;
    }
}
