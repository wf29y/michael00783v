package com.leon.datalink.resource.util.mqtt.entity;

public class MqttSubParam {

    private String topic;

    private Integer qos;

    private Boolean noLocal;

    private Boolean retainAsPublished;

    private Integer retainHandling;

    public MqttSubParam() {
    }

    public MqttSubParam(String topic, Integer qos) {
        this.topic = topic;
        this.qos = qos;
    }

    public MqttSubParam(String topic, Integer qos, Boolean noLocal, Boolean retainAsPublished, Integer retainHandling) {
        this.topic = topic;
        this.qos = qos;
        this.noLocal = noLocal;
        this.retainAsPublished = retainAsPublished;
        this.retainHandling = retainHandling;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getQos() {
        return qos;
    }

    public void setQos(Integer qos) {
        this.qos = qos;
    }

    public Boolean getNoLocal() {
        return noLocal;
    }

    public void setNoLocal(Boolean noLocal) {
        this.noLocal = noLocal;
    }

    public Boolean getRetainAsPublished() {
        return retainAsPublished;
    }

    public void setRetainAsPublished(Boolean retainAsPublished) {
        this.retainAsPublished = retainAsPublished;
    }

    public Integer getRetainHandling() {
        return retainHandling;
    }

    public void setRetainHandling(Integer retainHandling) {
        this.retainHandling = retainHandling;
    }
}
