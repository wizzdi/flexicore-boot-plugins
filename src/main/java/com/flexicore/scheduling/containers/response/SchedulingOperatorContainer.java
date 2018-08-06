package com.flexicore.scheduling.containers.response;

import java.util.List;

public class SchedulingOperatorContainer {

    private String canonicalName;

    public SchedulingOperatorContainer(String canonicalName, List<SchedulingMethod> schedulingMethodList) {
        this.canonicalName = canonicalName;
        this.schedulingMethodList = schedulingMethodList;
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

    private List<SchedulingMethod> schedulingMethodList


            ;
}
