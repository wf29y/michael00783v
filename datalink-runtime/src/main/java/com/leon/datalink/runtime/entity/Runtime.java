package com.leon.datalink.runtime.entity;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;
import com.leon.datalink.runtime.constants.Constants;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Map;

public class Runtime  implements Serializable {
    private static final long serialVersionUID = 1345156087085808964L;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private DateTime lastTime;

    private Long total;

    private Long analysisSuccessCount;

    private Long publishSuccessCount;

    private Long analysisFailCount;

    private Long publishFailCount;

    private LinkedList<RuntimeUpdateDataMsg> lastData;

    private Map<String, Object> variables;

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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getAnalysisSuccessCount() {
        return analysisSuccessCount;
    }

    public void setAnalysisSuccessCount(Long analysisSuccessCount) {
        this.analysisSuccessCount = analysisSuccessCount;
    }

    public Long getPublishSuccessCount() {
        return publishSuccessCount;
    }

    public void setPublishSuccessCount(Long publishSuccessCount) {
        this.publishSuccessCount = publishSuccessCount;
    }

    public Long getAnalysisFailCount() {
        return analysisFailCount;
    }

    public void setAnalysisFailCount(Long analysisFailCount) {
        this.analysisFailCount = analysisFailCount;
    }

    public Long getPublishFailCount() {
        return publishFailCount;
    }

    public void setPublishFailCount(Long publishFailCount) {
        this.publishFailCount = publishFailCount;
    }

    public LinkedList<RuntimeUpdateDataMsg> getLastData() {
        return lastData;
    }

    public void setLastData(LinkedList<RuntimeUpdateDataMsg> lastData) {
        this.lastData = lastData;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public void addLastData(RuntimeUpdateDataMsg runtimeUpdateDataMsg) {
        this.lastData.addFirst(runtimeUpdateDataMsg);
        if (lastData.size() > Constants.RULE_LAST_DATA_COUNT) {
            this.lastData.removeLast();
        }
    }

    public void addTotalCount() {
        this.total++;
    }

    public void addAnalysisSuccessCount() {
        this.analysisSuccessCount++;
    }

    public void addPublishSuccessCount() {
        this.publishSuccessCount++;
    }

    public void addAnalysisFailCount() {
        this.analysisFailCount++;
    }

    public void addPublishFailCount() {
        this.publishFailCount++;
    }


}
