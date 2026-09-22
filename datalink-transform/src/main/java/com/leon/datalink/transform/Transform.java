package com.leon.datalink.transform;

import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.transform.constants.TransformModeEnum;

import java.io.Serializable;

public class Transform implements Serializable {

    private TransformModeEnum transformMode;

    private ConfigProperties properties;

    private String transformRuntimeId; //转换运行时id

    private Integer workerNum;

    public TransformModeEnum getTransformMode() {
        return transformMode;
    }

    public Transform setTransformMode(TransformModeEnum transformMode) {
        this.transformMode = transformMode;
        return this;
    }

    public ConfigProperties getProperties() {
        return properties;
    }

    public Transform setProperties(ConfigProperties properties) {
        this.properties = properties;
        return this;
    }

    public String getTransformRuntimeId() {
        return transformRuntimeId;
    }

    public Transform setTransformRuntimeId(String transformRuntimeId) {
        this.transformRuntimeId = transformRuntimeId;
        return this;
    }

    public Integer getWorkerNum() {
        return workerNum;
    }

    public Transform setWorkerNum(Integer workerNum) {
        this.workerNum = workerNum;
        return this;
    }
}
