package com.leon.datalink.core.variable;

import java.io.Serializable;

public class Variable  implements Serializable {
    private static final long serialVersionUID = 1345156087665804264L;

    private String key;

    private Object value;

    private String desc;

    private VariableTypeEnum type;

    public Variable() {
    }
    public Variable(String key, Object value, String desc, VariableTypeEnum type) {
        this.key = key;
        this.value = value;
        this.desc = desc;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public VariableTypeEnum getType() {
        return type;
    }

    public void setType(VariableTypeEnum type) {
        this.type = type;
    }
}
