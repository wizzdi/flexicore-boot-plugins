package com.flexicore.scheduling.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.data.jsoncontainers.FilteringInformationHolder;
import com.flexicore.scheduling.model.Schedule;

public class SchedulingActionFiltering extends FilteringInformationHolder {

    private String scheduleId;
    @JsonIgnore
    private Schedule schedule;


    public String getScheduleId() {
        return scheduleId;
    }

    public SchedulingActionFiltering setScheduleId(String scheduleId) {
        this.scheduleId = scheduleId;
        return this;
    }


    @JsonIgnore
    public Schedule getSchedule() {
        return schedule;
    }

    public SchedulingActionFiltering setSchedule(Schedule schedule) {
        this.schedule = schedule;
        return this;
    }
}
