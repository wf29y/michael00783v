package com.leon.datalink.driver.actor;

import akka.actor.ActorRef;
import com.leon.datalink.runtime.actor.RuntimeUpdateDataMsg;


public class PublishDataMsg {

    private Object data;

    private ActorRef runtimeRef;

    private RuntimeUpdateDataMsg runtimeUpdateDataMsg;

    public PublishDataMsg(Object data, ActorRef runtimeRef, RuntimeUpdateDataMsg runtimeUpdateDataMsg) {
        this.data = data;
        this.runtimeRef = runtimeRef;
        this.runtimeUpdateDataMsg = runtimeUpdateDataMsg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ActorRef getRuntimeRef() {
        return runtimeRef;
    }

    public void setRuntimeRef(ActorRef runtimeRef) {
        this.runtimeRef = runtimeRef;
    }

    public RuntimeUpdateDataMsg getRuntimeUpdateDataMsg() {
        return runtimeUpdateDataMsg;
    }

    public void setRuntimeUpdateDataMsg(RuntimeUpdateDataMsg runtimeUpdateDataMsg) {
        this.runtimeUpdateDataMsg = runtimeUpdateDataMsg;
    }
}
