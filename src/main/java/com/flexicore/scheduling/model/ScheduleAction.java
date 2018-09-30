package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.BaseclassIdFiltering;
import com.flexicore.model.dynamic.ExecutionParametersHolder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ScheduleAction extends Baseclass {
    private static ScheduleAction s_Singleton=new ScheduleAction();
    public  static ScheduleAction s() {return s_Singleton;}


    @JsonIgnore
    @OneToMany(targetEntity = ScheduleToAction.class,mappedBy = "rightside",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<ScheduleToAction> scheduleToActions=new ArrayList<>();
    @OneToMany(targetEntity = BaseclassIdFiltering.class,mappedBy = "filteringInformationHolder",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<BaseclassIdFiltering> serviceCanonicalNames=new ArrayList<>();
    private String methodName;

    @OneToOne(targetEntity = ExecutionParametersHolder.class)
    private ExecutionParametersHolder executionParametersHolder;

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleToAction.class,mappedBy = "rightside",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    public List<ScheduleToAction> getScheduleToActions() {
        return scheduleToActions;
    }

    public ScheduleAction setScheduleToActions(List<ScheduleToAction> scheduleToActions) {
        this.scheduleToActions = scheduleToActions;
        return this;
    }


    @OneToMany(targetEntity = BaseclassIdFiltering.class,mappedBy = "filteringInformationHolder",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    public List<BaseclassIdFiltering> getServiceCanonicalNames() {
        return serviceCanonicalNames;
    }

    public ScheduleAction setServiceCanonicalNames(List<BaseclassIdFiltering> serviceCanonicalNames) {
        this.serviceCanonicalNames = serviceCanonicalNames;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ScheduleAction setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }


    @OneToOne(targetEntity = ExecutionParametersHolder.class)
    public ExecutionParametersHolder getExecutionParametersHolder() {
        return executionParametersHolder;
    }

    public ScheduleAction setExecutionParametersHolder(ExecutionParametersHolder executionParametersHolder) {
        this.executionParametersHolder = executionParametersHolder;
        return this;
    }
}
