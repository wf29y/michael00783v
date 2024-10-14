package com.leon.datalink.rule.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.leon.datalink.rule.constants.Constants;

import java.util.LinkedList;

public class RuleRuntime {

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime lastTime;

    private Long successCount;

    private Long failCount;

    private LinkedList<RuleData> lastData;

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

    public void addSuccess() {
        this.successCount++;
    }

    public void addFail() {
        this.failCount++;
    }

    public void addLastData(Object data) {
        RuleData ruleData = new RuleData();
        ruleData.setData(data);
        ruleData.setTime(DateTime.now());
        this.lastData.addFirst(ruleData);
        if (lastData.size() > Constants.RULE_LAST_DATA_COUNT) {
            this.lastData.removeLast();
        }
    }
}
