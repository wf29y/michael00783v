package com.leon.datalink.runtime.entity;

import com.leon.datalink.cluster.ClusterMemberManager;
import com.leon.datalink.core.serializer.ProtostuffSerializable;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;

public class RuntimeStatus implements ProtostuffSerializable {

    private RuntimeTypeEnum type;

    private RuntimeStatusEnum status;

    private String message;

    private String entityRuntimeId;

    private String memberName = ClusterMemberManager.getLocalMemberName();

    public RuntimeStatus() {
    }

    public RuntimeStatus(RuntimeTypeEnum type,String entityRuntimeId) {
        this.type = type;
        this.entityRuntimeId = entityRuntimeId;
    }

    public RuntimeStatus(RuntimeTypeEnum type,RuntimeStatusEnum status,String entityRuntimeId) {
        this.type = type;
        this.status = status;
        this.entityRuntimeId = entityRuntimeId;
    }

    public RuntimeStatus(RuntimeTypeEnum type,RuntimeStatusEnum status,String message,String entityRuntimeId) {
        this.type = type;
        this.status = status;
        this.message = message;
        this.entityRuntimeId = entityRuntimeId;
    }

    public void init(){
        this.status = RuntimeStatusEnum.INIT;
    }

    public void normal(){
        this.status = RuntimeStatusEnum.NORMAL;
    }

    public void abnormal(String message){
        this.status = RuntimeStatusEnum.ABNORMAL;
        this.message = message;
    }


    public RuntimeTypeEnum getType() {
        return type;
    }

    public void setType(RuntimeTypeEnum type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public RuntimeStatusEnum getStatus() {
        return status;
    }

    public void setStatus(RuntimeStatusEnum status) {
        this.status = status;
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
