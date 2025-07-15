package com.leon.datalink.web.model;

import java.lang.ref.PhantomReference;

/**
 * @ClassName SystemInfo
 * @Description
 * @Author Leon
 * @Date2022/4/11 10:35
 * @Version V1.0
 **/
public class SystemInfo {

    private String version;

    private String ip;

    private String time;

    private String username;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
