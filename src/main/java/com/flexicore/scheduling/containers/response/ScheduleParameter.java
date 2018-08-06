package com.flexicore.scheduling.containers.response;

import com.flexicore.scheduling.interfaces.SchedulingParameter;

public class ScheduleParameter {

    private String displayName;
    private String description;

    public ScheduleParameter(SchedulingParameter schedulingParameter) {
        this.displayName=schedulingParameter.displayName();
        this.description=schedulingParameter.description();
    }

    public String getDisplayName() {
        return displayName;
    }

    public ScheduleParameter setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public ScheduleParameter setDescription(String description) {
        this.description = description;
        return this;
    }
}
