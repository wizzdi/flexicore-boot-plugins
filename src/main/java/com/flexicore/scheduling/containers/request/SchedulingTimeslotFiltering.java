package com.flexicore.scheduling.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.FilteringInformationHolder;
import com.flexicore.scheduling.model.Schedule;

public class SchedulingTimeslotFiltering extends FilteringInformationHolder {

	private String scheduleId;
	@JsonIgnore
	private Schedule schedule;

	public String getScheduleId() {
		return scheduleId;
	}

	public SchedulingTimeslotFiltering setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
		return this;
	}

	@JsonIgnore
	public Schedule getSchedule() {
		return schedule;
	}

	public SchedulingTimeslotFiltering setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}
}
