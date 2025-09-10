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

    private List<Resource> sourceResourceList;

    private List<Resource> destResourceList;

    private TransformModeEnum transformMode;

    private String script;

    private String pluginId;

    private Plugin plugin;

    private Map<String, Object> variables;

    private boolean enable = false;

    private boolean ignoreNullValue;

    private String searchResourceId;

    public String getRuleId() {
        return ruleId;
    }

    public Rule setRuleId(String ruleId) {
        this.ruleId = ruleId;
        return this;
    }

    public List<Resource> getSourceResourceList() {
        return sourceResourceList;
    }

    public void setSourceResourceList(List<Resource> sourceResourceList) {
        this.sourceResourceList = sourceResourceList;
    }

    public List<Resource> getDestResourceList() {
        return destResourceList;
    }

    public Rule setDestResourceList(List<Resource> destResourceList) {
        this.destResourceList = destResourceList;
        return this;
    }

    public String getRuleName() {
        return ruleName;
    }

    public Rule setRuleName(String ruleName) {
        this.ruleName = ruleName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Rule setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getScript() {
        return script;
    }

    public Rule setScript(String script) {
        this.script = script;
        return this;
    }

    public TransformModeEnum getTransformMode() {
        return transformMode;
    }

    public Rule setTransformMode(TransformModeEnum transformMode) {
        this.transformMode = transformMode;
        return this;
    }

    public boolean isEnable() {
        return enable;
    }

    public Rule setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public boolean isIgnoreNullValue() {
        return ignoreNullValue;
    }

    public Rule setIgnoreNullValue(boolean ignoreNullValue) {
        this.ignoreNullValue = ignoreNullValue;
        return this;
    }

    public String getPluginId() {
        return pluginId;
    }

    public Rule setPluginId(String pluginId) {
        this.pluginId = pluginId;
        return this;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public Rule setPlugin(Plugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public Rule setVariables(Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    public String getSearchResourceId() {
        return searchResourceId;
    }

    public Rule setSearchResourceId(String searchResourceId) {
        this.searchResourceId = searchResourceId;
        return this;
    }
}
