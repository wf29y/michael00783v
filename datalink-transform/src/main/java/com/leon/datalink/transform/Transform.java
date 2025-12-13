package com.leon.datalink.transform;

import com.leon.datalink.core.config.ConfigProperties;
import com.leon.datalink.transform.constants.TransformModeEnum;

public class Transform {

    private String ruleId;

    private TransformModeEnum transformMode;

    private ConfigProperties properties;

    private String transformRuntimeId; //转换运行时id

    public String getRuleId() {
        return ruleId;
    }

    public Transform setRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

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
}
