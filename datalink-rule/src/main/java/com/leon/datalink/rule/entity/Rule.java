package com.leon.datalink.rule.entity;

import com.leon.datalink.transform.Transform;
import com.leon.datalink.resource.Resource;

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

    private List<Transform> transformList;

    private boolean enable = false;

    private String searchResourceId;

    private String searchPluginId;

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

    public boolean isEnable() {
        return enable;
    }

    public Rule setEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public List<Transform> getTransformList() {
        return transformList;
    }

    public Rule setTransformList(List<Transform> transformList) {
        this.transformList = transformList;
        return this;
    }

    public String getSearchResourceId() {
        return searchResourceId;
    }

    public Rule setSearchResourceId(String searchResourceId) {
        this.searchResourceId = searchResourceId;
        return this;
    }

    public String getSearchPluginId() {
        return searchPluginId;
    }

    public Rule setSearchPluginId(String searchPluginId) {
        this.searchPluginId = searchPluginId;
        return this;
    }
}
