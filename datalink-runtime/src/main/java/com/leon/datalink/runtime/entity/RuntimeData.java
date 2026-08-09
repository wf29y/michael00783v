package com.leon.datalink.runtime.entity;

import cn.hutool.core.date.DateUtil;
import com.leon.datalink.cluster.member.ClusterMemberManager;
import com.leon.datalink.core.serializer.ProtostuffSerializable;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;

public class RuntimeData implements ProtostuffSerializable {

    private RuntimeTypeEnum type;

    private String entityRuntimeId;

    private Object data;

    private String time;

    private boolean error;

    private String message;

    private String memberName = ClusterMemberManager.getLocalMemberName();

    public RuntimeData() {
    }

    public RuntimeData(RuntimeTypeEnum type) {
        this.type = type;
    }

    public RuntimeData(RuntimeTypeEnum type,String entityRuntimeId) {
        this.type = type;
        this.entityRuntimeId = entityRuntimeId;
    }

    public RuntimeData success(Object data) {
        this.data = data;
        this.time = DateUtil.now();
        this.error = false;
        return this;
    }

    public RuntimeData fail(String message) {
        this.message = message;
        this.time = DateUtil.now();
        this.error = true;
        return this;
    }


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RuntimeTypeEnum getType() {
        return type;
    }

    public void setType(RuntimeTypeEnum type) {
        this.type = type;
    }

    public String getEntityRuntimeId() {
        return entityRuntimeId;
    }

    public void setEntityRuntimeId(String entityRuntimeId) {
        this.entityRuntimeId = entityRuntimeId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }
}
