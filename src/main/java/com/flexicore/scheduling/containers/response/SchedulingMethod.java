package com.flexicore.scheduling.containers.response;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchedulingMethod {

    private String methodName;
    private String displayName;
    private String description;
    private List<ScheduleParameter> scheduleParameterList;

    public SchedulingMethod(String methodName, com.flexicore.scheduling.interfaces.SchedulingMethod schedulingMethod) {
        this.methodName = methodName;
        this.displayName=schedulingMethod.displayName();
        this.description = schedulingMethod.description();
        this.scheduleParameterList= Stream.of(schedulingMethod.parameters()).map(f->new ScheduleParameter(f)).collect(Collectors.toList());
    }

    public String getMethodName() {
        return methodName;
    }

    public SchedulingMethod setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public SchedulingMethod setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public SchedulingMethod setDisplayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public List<ScheduleParameter> getScheduleParameterList() {
        return scheduleParameterList;
    }

    public SchedulingMethod setScheduleParameterList(List<ScheduleParameter> scheduleParameterList) {
        this.scheduleParameterList = scheduleParameterList;
        return this;
    }
}
