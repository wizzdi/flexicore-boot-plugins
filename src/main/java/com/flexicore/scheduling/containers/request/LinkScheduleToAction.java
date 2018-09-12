package com.flexicore.scheduling.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleAction;

public class LinkScheduleToAction {

    private String scheduleId;
    @JsonIgnore
    private Schedule schedule;
    private String actionId;
    @JsonIgnore
    private ScheduleAction scheduleAction;




    public String getScheduleId() {
        return scheduleId;
    }

    public LinkScheduleToAction setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
        return this;
    }
    @JsonIgnore
    public Schedule getSchedule() {
        return schedule;
    }

    public LinkScheduleToAction setSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public String getActionId() {
        return actionId;
    }

    public LinkScheduleToAction setActionId(String actionId) {
        this.actionId = actionId;
        return this;
    }
    @JsonIgnore
    public ScheduleAction getScheduleAction() {
        return scheduleAction;
    }

    public LinkScheduleToAction setScheduleAction(ScheduleAction scheduleAction) {
        this.scheduleAction = scheduleAction;
        return this;
    }
}
