package com.leon.datalink.resource.util.mqtt;


public class MqttClientConfig {

    private String hostUrl;

    private String username;

    private String password;

    private Integer connectionTimeout;

    private Integer keepAliveInterval;

    private Boolean ssl;

    private Boolean autoReconnect;

    private int mqttVersion;

    public MqttClientConfig() {
    }

    public String getHostUrl() {
        return hostUrl;
    }

    public MqttClientConfig setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public MqttClientConfig setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public MqttClientConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public MqttClientConfig setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public Integer getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public MqttClientConfig setKeepAliveInterval(Integer keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
        return this;
    }

    public Boolean getAutoReconnect() {
        return autoReconnect;
    }

    public void setAutoReconnect(Boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

    public int getMqttVersion() {
        return mqttVersion;
    }

    public void setMqttVersion(int mqttVersion) {
        this.mqttVersion = mqttVersion;
    }
}
