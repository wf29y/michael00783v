package com.leon.datalink.driver.mqtt;

import com.leon.datalink.core.utils.JacksonUtils;

public class MqttData  {

    private String topic;

    private String payload;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return JacksonUtils.toJson(this);
    }
}
