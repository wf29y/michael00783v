package com.leon.datalink.transform.plugin;

import java.io.Serializable;

public class Plugin implements Serializable {
    private static final long serialVersionUID = 1345166087085804264L;

    private String pluginId;

    private String pluginName;

    private String description;

    private String packagePath;

    private String updateTime;

    public String getPluginId() {
        return pluginId;
    }

    public Plugin setPluginId(String pluginId) {
        this.pluginId = pluginId;
        return this;
    }

    public String getPluginName() {
        return pluginName;
    }

    public Plugin setPluginName(String pluginName) {
        this.pluginName = pluginName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Plugin setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getPackagePath() {
        return packagePath;
    }

    public Plugin setPackagePath(String packagePath) {
        this.packagePath = packagePath;
        return this;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public Plugin setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }
}
