package com.leon.datalink.runtime.entity;

import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;

import java.io.Serializable;

public class RuntimeStatus implements Serializable {
    private static final long serialVersionUID = 1895166087085806666L;

    private RuntimeTypeEnum type;

    private RuntimeStatusEnum status;

    private String errorMessage;

    private String entityRuntimeId;

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

    public RuntimeStatus(RuntimeTypeEnum type,RuntimeStatusEnum status,String errorMessage,String entityRuntimeId) {
        this.type = type;
        this.status = status;
        this.errorMessage = errorMessage;
        this.entityRuntimeId = entityRuntimeId;
    }

    public void init(){
        this.status = RuntimeStatusEnum.INIT;
    }

    public void normal(){
        this.status = RuntimeStatusEnum.NORMAL;
    }

    public void abnormal(String errorMessage){
        this.status = RuntimeStatusEnum.ABNORMAL;
        this.errorMessage = errorMessage;
    }


    public RuntimeTypeEnum getType() {
        return type;
    }

    public void setType(RuntimeTypeEnum type) {
        this.type = type;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
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
}
