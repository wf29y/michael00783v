package com.leon.datalink.core.schedule;

import com.leon.datalink.core.serializer.ProtostuffSerializable;

public class Schedule implements ProtostuffSerializable {

    private String scheduleName;

    private String ruleId;

    private String resourceName;

    private Long initialDelay;

    private String initialDelayUnit;

    private String cronExpression;

    private String createTime;

    public String getScheduleName() {
        return scheduleName;
    }

    public void setScheduleName(String scheduleName) {
        this.scheduleName = scheduleName;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public Long getInitialDelay() {
        return initialDelay;
    }

    public void setInitialDelay(Long initialDelay) {
        this.initialDelay = initialDelay;
    }

    public String getInitialDelayUnit() {
        return initialDelayUnit;
    }

    public void setInitialDelayUnit(String initialDelayUnit) {
        this.initialDelayUnit = initialDelayUnit;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
