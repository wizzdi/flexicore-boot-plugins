package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.FilteringInformationHolder;

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
    private String serviceCanonicalName;
    private String methodName;
    @Lob
    private String parameter1;
    @Lob
    private String parameter2;
    @Lob
    private String parameter3;

    @OneToOne(targetEntity = FilteringInformationHolder.class)
    private FilteringInformationHolder filteringInformationHolder;

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleToAction.class,mappedBy = "rightside",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    public List<ScheduleToAction> getScheduleToActions() {
        return scheduleToActions;
    }

    public ScheduleAction setScheduleToActions(List<ScheduleToAction> scheduleToActions) {
        this.scheduleToActions = scheduleToActions;
        return this;
    }

    public String getServiceCanonicalName() {
        return serviceCanonicalName;
    }

    public ScheduleAction setServiceCanonicalName(String serviceCanonicalName) {
        this.serviceCanonicalName = serviceCanonicalName;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public ScheduleAction setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }
    @Lob
    public String getParameter1() {
        return parameter1;
    }

    public ScheduleAction setParameter1(String parameter1) {
        this.parameter1 = parameter1;
        return this;
    }
    @Lob
    public String getParameter2() {
        return parameter2;
    }

    public ScheduleAction setParameter2(String parameter2) {
        this.parameter2 = parameter2;
        return this;
    }
    @Lob
    public String getParameter3() {
        return parameter3;
    }

    public ScheduleAction setParameter3(String parameter3) {
        this.parameter3 = parameter3;
        return this;
    }

    @OneToOne(targetEntity = FilteringInformationHolder.class)
    public FilteringInformationHolder getFilteringInformationHolder() {
        return filteringInformationHolder;
    }

    public ScheduleAction setFilteringInformationHolder(FilteringInformationHolder filteringInformationHolder) {
        this.filteringInformationHolder = filteringInformationHolder;
        return this;
    }
}
