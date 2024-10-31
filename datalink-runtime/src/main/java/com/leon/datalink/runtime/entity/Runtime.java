package com.leon.datalink.runtime.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.leon.datalink.runtime.constants.Constants;

import java.util.LinkedList;
import java.util.Map;

public class Runtime {

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime lastTime;

    private Long successCount;

    private Long failCount;

    private LinkedList<RuleData> lastData;

    private Map<String,Object> variables;

    static class RuleData {

        @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
        private DateTime time;

        private Object data;

        public DateTime getTime() {
            return time;
        }

        public void setTime(DateTime time) {
            this.time = time;
        }

        public Object getData() {
            return data;
        }

        public void setData(Object data) {
            this.data = data;
        }
    }

    public DateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(DateTime startTime) {
        this.startTime = startTime;
    }

    public DateTime getLastTime() {
        return lastTime;
    }

    public void setLastTime(DateTime lastTime) {
        this.lastTime = lastTime;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getFailCount() {
        return failCount;
    }

    public void setFailCount(Long failCount) {
        this.failCount = failCount;
    }

    public LinkedList<RuleData> getLastData() {
        return lastData;
    }

    public void setLastData(LinkedList<RuleData> lastData) {
        this.lastData = lastData;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public void addSuccess() {
        this.successCount++;
    }

    public void addFail() {
        this.failCount++;
    }

    public void addLastData(Object data,DateTime time) {
        RuleData ruleData = new RuleData();
        ruleData.setData(data);
        ruleData.setTime(time);
        this.lastData.addFirst(ruleData);
        if (lastData.size() > Constants.RULE_LAST_DATA_COUNT) {
            this.lastData.removeLast();
        }
    }
}
