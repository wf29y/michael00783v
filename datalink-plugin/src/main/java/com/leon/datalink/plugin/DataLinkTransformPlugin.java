package com.leon.datalink.plugin;

import java.util.HashMap;
import java.util.Map;

public abstract class DataLinkTransformPlugin implements DataLinkPlugin {

    private Map<String, Object> variable = new HashMap<>();

    public abstract Object transform(String json);

    public void setVariable(Map<String, Object> variable) {
        this.variable = variable;
    }

    public Map<String, Object> getVariable() {
        return variable;
    }

    public void updateVariable(String key, String value) {
        if (variable.containsKey(key)) variable.put(key, value);
    }


}
