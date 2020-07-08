package com.flexicore.scheduling.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.Schedule;
import com.flexicore.scheduling.model.ScheduleTimeslot;

public class UpdateTimeslot extends CreateTimeslot {
	private String scheduleTimeslotId;
	@JsonIgnore
	private ScheduleTimeslot scheduleTimeslot;

	public String getScheduleTimeslotId() {
		return scheduleTimeslotId;
	}

	public UpdateTimeslot setScheduleTimeslotId(String scheduleTimeslotId) {
		this.scheduleTimeslotId = scheduleTimeslotId;
		return this;
	}

	@JsonIgnore
	public ScheduleTimeslot getScheduleTimeslot() {
		return scheduleTimeslot;
	}

	public UpdateTimeslot setScheduleTimeslot(ScheduleTimeslot scheduleTimeslot) {
		this.scheduleTimeslot = scheduleTimeslot;
		return this;
	}

}
