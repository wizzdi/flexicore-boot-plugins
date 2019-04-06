package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.model.dynamic.ExecutionParametersHolder;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ScheduleAction extends DynamicExecution {
    private static ScheduleAction s_Singleton=new ScheduleAction();
    public  static ScheduleAction s() {return s_Singleton;}

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleToAction.class,mappedBy = "rightside",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    private List<ScheduleToAction> scheduleToActions=new ArrayList<>();

    @JsonIgnore
    @OneToMany(targetEntity = ScheduleToAction.class,mappedBy = "rightside",cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    public List<ScheduleToAction> getScheduleToActions() {
        return scheduleToActions;
    }

    public ScheduleAction setScheduleToActions(List<ScheduleToAction> scheduleToActions) {
        this.scheduleToActions = scheduleToActions;
        return this;
    }
}
