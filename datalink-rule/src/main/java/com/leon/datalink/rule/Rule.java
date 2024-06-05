package com.leon.datalink.rule;

import com.leon.datalink.resource.Resource;

import java.io.Serializable;
import java.util.List;

public class Rule implements Serializable {
    private static final long serialVersionUID = 1345156087085804264L;

    private String ruleId;

    private String ruleName;

    private String description;

    private Resource sourceResource;

    private List<Resource> destResourceList;

    private String script;

    private boolean enable;

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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
