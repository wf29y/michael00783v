package com.leon.datalink.resource.constans;

/**
 * 数据源的模式
 */
public enum SourceModeEnum {

    SUBSCRIBE, // 订阅型 - 被动接收数据 集群中仅有一个实例
    LISTEN, // 监听型 - 被动接收数据 集群中可有多个实例
    SCHEDULE; // 定时调度型 - 定时拉取数据

    SourceModeEnum() {
    }

}
