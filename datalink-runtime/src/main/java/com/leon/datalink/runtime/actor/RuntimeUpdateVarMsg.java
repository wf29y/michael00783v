package com.leon.datalink.runtime.actor;

import java.util.Map;

public class RuntimeUpdateVarMsg {

    private Map<String,Object> variables;


    public RuntimeUpdateVarMsg(Map<String, Object> variables) {
        this.variables = variables;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
