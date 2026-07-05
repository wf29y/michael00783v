package com.leon.datalink.core.listener;

import java.io.Serializable;

public class Listener  implements Serializable {
    private static final long serialVersionUID = 1333156087665804264L;

    private String ip;

    private Integer port;

    private ListenerTypeEnum type;

    private String desc;

    public Listener() {
    }

    public Listener(String ip,Integer port, ListenerTypeEnum type, String desc) {
        this.ip = ip;
        this.port = port;
        this.type = type;
        this.desc = desc;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public ListenerTypeEnum getType() {
        return type;
    }

    public void setType(ListenerTypeEnum type) {
        this.type = type;
    }


}
