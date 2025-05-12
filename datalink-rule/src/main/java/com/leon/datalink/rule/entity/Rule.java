package com.leon.datalink.rule.entity;

import com.leon.datalink.resource.Resource;
import com.leon.datalink.rule.constants.TransformModeEnum;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Rule implements Serializable {
    private static final long serialVersionUID = 1345156087085804264L;

    private String ruleId;

    private String ruleName;

    private String description;

    private Resource sourceResource;

    private List<Resource> destResourceList;

    private TransformModeEnum transformMode;

    private String script;

    private String pluginId;

    private Plugin plugin;

    private Map<String,Object> variables;

    private boolean enable = false;

    private boolean ignoreNullValue;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public Resource getSourceResource() {
        return sourceResource;
    }

    public void setSourceResource(Resource sourceResource) {
        this.sourceResource = sourceResource;
    }

    public List<Resource> getDestResourceList() {
        return destResourceList;
    }

    public void setDestResourceList(List<Resource> destResourceList) {
        this.destResourceList = destResourceList;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public TransformModeEnum getTransformMode() {
        return transformMode;
    }

    public void setTransformMode(TransformModeEnum transformMode) {
        this.transformMode = transformMode;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isIgnoreNullValue() {
        return ignoreNullValue;
    }

    public void setIgnoreNullValue(boolean ignoreNullValue) {
        this.ignoreNullValue = ignoreNullValue;
    }

    public String getPluginId() {
        return pluginId;
    }

    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }
}
