package com.leon.datalink.runtime.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Map;

public class Runtime implements Serializable {
    private static final long serialVersionUID = 1345156087085808964L;

    // 启动时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime startTime;

    // 数据源运行状态
    private Map<String, RuntimeEntity> sourceRuntimeList;

    // 目的资源运行状态
    private Map<String, RuntimeEntity> destRuntimeList;

    // 转换运行状态
    private RuntimeEntity transformRuntime;

    // 变量
    private Map<String, Object> variables;


    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public Map<String, RuntimeEntity> getSourceRuntimeList() {
        return sourceRuntimeList;
    }

    public void setSourceRuntimeList(Map<String, RuntimeEntity> sourceRuntimeList) {
        this.sourceRuntimeList = sourceRuntimeList;
    }

    public Map<String, RuntimeEntity> getDestRuntimeList() {
        return destRuntimeList;
    }

    public void setDestRuntimeList(Map<String, RuntimeEntity> destRuntimeList) {
        this.destRuntimeList = destRuntimeList;
    }

    public RuntimeEntity getTransformRuntime() {
        return transformRuntime;
    }

    public void setTransformRuntime(RuntimeEntity transformRuntime) {
        this.transformRuntime = transformRuntime;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

}
