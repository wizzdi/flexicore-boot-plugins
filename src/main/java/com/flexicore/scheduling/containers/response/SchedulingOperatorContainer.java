package com.flexicore.scheduling.containers.response;

import java.util.List;

public class SchedulingOperatorContainer {

    private String canonicalName;
    private List<SchedulingMethod> schedulingMethodList;
    private String displayName;
    private String description;

    public SchedulingOperatorContainer(String canonicalName,String displayName,String description, List<SchedulingMethod> schedulingMethodList) {
        this.canonicalName = canonicalName;
        this.schedulingMethodList = schedulingMethodList;
        this.displayName=displayName;
        this.description=description;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public SchedulingOperatorContainer setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
        return this;
    }

    public List<SchedulingMethod> getSchedulingMethodList() {
        return schedulingMethodList;
    }

    public SchedulingOperatorContainer setSchedulingMethodList(List<SchedulingMethod> schedulingMethodList) {
        this.schedulingMethodList = schedulingMethodList;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SchedulingOperatorContainer setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SchedulingOperatorContainer setDescription(String description) {
        this.description = description;
        return this;
    }
}
