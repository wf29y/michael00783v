package com.leon.datalink.runtime.entity;

import cn.hutool.core.date.DateTime;
import com.leon.datalink.runtime.constants.RuntimeStatusEnum;
import com.leon.datalink.runtime.constants.RuntimeTypeEnum;
import org.omg.CORBA.PUBLIC_MEMBER;

public class RuntimeStatus {

    private RuntimeTypeEnum type;

    private RuntimeStatusEnum status;

    private String errorMessage;

    public RuntimeStatus() {
    }

    public RuntimeStatus(RuntimeTypeEnum type) {
        this.type = type;
    }

    public RuntimeStatus(RuntimeTypeEnum type,RuntimeStatusEnum status) {
        this.type = type;
        this.status = status;
    }

    public RuntimeStatus(RuntimeTypeEnum type,RuntimeStatusEnum status,String errorMessage) {
        this.type = type;
        this.status = status;
        this.errorMessage = errorMessage;
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
}
