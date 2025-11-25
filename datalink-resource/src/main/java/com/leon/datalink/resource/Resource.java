package com.leon.datalink.resource;

import com.leon.datalink.driver.entity.DriverProperties;

import java.io.Serializable;

public class Resource implements Serializable {
    private static final long serialVersionUID = 1276156087085594264L;

    private String resourceId;

    private String resourceName;

    private String description;

    private ResourceTypeEnum resourceType;

    private DriverProperties properties;

    private String resourceRuntimeId; //资源运行时id

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DriverProperties getProperties() {
        return properties;
    }

    public void setProperties(DriverProperties properties) {
        this.properties = properties;
    }

    public ResourceTypeEnum getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceRuntimeId() {
        return resourceRuntimeId;
    }

    public void setResourceRuntimeId(String resourceRuntimeId) {
        this.resourceRuntimeId = resourceRuntimeId;
    }
}
