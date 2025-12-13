package com.leon.datalink.transform.script;


import java.io.Serializable;
import java.util.Map;

public class Script implements Serializable {
    private static final long serialVersionUID = 1345156089995804264L;

    private String scriptId;

    private String scriptName;

    private String scriptContent;

    private String paramContent;

    private String resultContent;

    Map<String, Object> variables;

    private String description;

    private String updateTime;

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

    public String getParamContent() {
        return paramContent;
    }

    public void setParamContent(String paramContent) {
        this.paramContent = paramContent;
    }

    public String getResultContent() {
        return resultContent;
    }

    public void setResultContent(String resultContent) {
        this.resultContent = resultContent;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
