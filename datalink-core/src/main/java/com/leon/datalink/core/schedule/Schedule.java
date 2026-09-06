package com.leon.datalink.core.schedule;

import akka.actor.Cancellable;
import com.leon.datalink.core.serializer.ProtostuffSerializable;

public class Schedule implements ProtostuffSerializable {

    private String id;

    private String ruleId;

    private String resourceName;

    private Long initialDelay;

    private String initialDelayUnit;

    private Long interval;

    private String intervalUnit;

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

    public String getInitialDelayUnit() {
        return initialDelayUnit;
    }

    public Schedule setInitialDelayUnit(String initialDelayUnit) {
        this.initialDelayUnit = initialDelayUnit;
        return this;
    }

    public Long getInterval() {
        return interval;
    }

    public Schedule setInterval(Long interval) {
        this.interval = interval;
        return this;
    }

    public String getIntervalUnit() {
        return intervalUnit;
    }

    public Schedule setIntervalUnit(String intervalUnit) {
        this.intervalUnit = intervalUnit;
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
