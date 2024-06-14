package com.leon.datalink.rule.script;


import java.io.Serializable;

public class Script implements Serializable {
    private static final long serialVersionUID = 1345156089995804264L;

    private String scriptId;

    private String scriptName;

    private String scriptContent;

    private String description;

    public String getScriptId() {
        return scriptId;
    }

    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
