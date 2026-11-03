package com.leon.datalink.resource.constans;

/**
 * 数据源模式
 */
public enum SourceModeEnum {

    /**
     * 订阅型 - 集群单实例
     */
    SUBSCRIBE_SINGLE,

    /**
     * 订阅型 - 集群多实例
     */
    SUBSCRIBE_MULTI,

    /**
     * 监听型 - 集群多实例
     */
    LISTEN,

    /**
     * 定时调度型 - 集群多实例
     */
    SCHEDULE

}
