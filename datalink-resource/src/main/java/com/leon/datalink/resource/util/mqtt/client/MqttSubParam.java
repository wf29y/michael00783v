package com.leon.datalink.resource.util.mqtt.client;

public class MqttSubParam {

    private String topic;

    private int qos;

    private boolean noLocal;

    private boolean retainAsPublished;

    private int retainHandling;

    public MqttSubParam() {
    }

    public MqttSubParam(String topic, int qos) {
        this.topic = topic;
        this.qos = qos;
    }

    public MqttSubParam(String topic, int qos, boolean noLocal, boolean retainAsPublished, int retainHandling) {
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

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }

    public boolean isNoLocal() {
        return noLocal;
    }

    public void setNoLocal(boolean noLocal) {
        this.noLocal = noLocal;
    }

    public boolean isRetainAsPublished() {
        return retainAsPublished;
    }

    public void setRetainAsPublished(boolean retainAsPublished) {
        this.retainAsPublished = retainAsPublished;
    }

    public int getRetainHandling() {
        return retainHandling;
    }

    public void setRetainHandling(int retainHandling) {
        this.retainHandling = retainHandling;
    }

}
