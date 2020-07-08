package com.flexicore.scheduling.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;

public class ExecuteScheduleRequest {

	private String scheduleId;

	@JsonIgnore
	private Schedule schedule;

	public String getScheduleId() {
		return scheduleId;
	}

	public ExecuteScheduleRequest setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
		return this;
	}

	@JsonIgnore
	public Schedule getSchedule() {
		return schedule;
	}

	public ExecuteScheduleRequest setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return this;
	}
}
