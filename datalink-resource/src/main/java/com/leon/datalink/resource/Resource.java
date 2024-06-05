package com.leon.datalink.resource;

import java.io.Serializable;
import java.util.Map;

public class Resource implements Serializable {
    private static final long serialVersionUID = 1276156087085594264L;

    private String resourceId;

    private String resourceName;

    private String description;

    private ResourceTypeEnum resourceType;

    private Map<String, Object> properties;

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

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public ResourceTypeEnum getResourceType() {
        return resourceType;
    }
    public void setResourceType(ResourceTypeEnum resourceType) {
        this.resourceType = resourceType;
    }

}
