package com.leon.datalink.core.schedule;

import akka.actor.Cancellable;
import com.leon.datalink.core.serializer.ProtostuffSerializable;

public class Schedule implements ProtostuffSerializable {

    private String id;

    private String ruleId;

    private String resourceName;

    private Long initialDelay;

    private Long period;

    private String timeUnit;

    private String createTime;

    private Cancellable cancellable;

    public String getId() {
        return id;
    }

    public Schedule setId(String id) {
        this.id = id;
        return this;
    }

    public String getRuleId() {
        return ruleId;
    }

    public Schedule setRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public String getResourceName() {
        return resourceName;
    }

    public Schedule setResourceName(String resourceName) {
        this.resourceName = resourceName;
        return this;
    }

    public Long getInitialDelay() {
        return initialDelay;
    }

    public Schedule setInitialDelay(Long initialDelay) {
        this.initialDelay = initialDelay;
        return this;
    }

    public Long getPeriod() {
        return period;
    }

    public Schedule setPeriod(Long period) {
        this.period = period;
        return this;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public Schedule setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public String getCreateTime() {
        return createTime;
    }

    public Schedule setCreateTime(String createTime) {
        this.createTime = createTime;
        return this;
    }

    public Cancellable getCancellable() {
        return cancellable;
    }

    public Schedule setCancellable(Cancellable cancellable) {
        this.cancellable = cancellable;
        return this;
    }
}
