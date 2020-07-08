package com.flexicore.scheduling.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.scheduling.model.ScheduleAction;

public class UpdateSchedulingAction extends CreateSchedulingAction {

	private String scheduleActionId;
	@JsonIgnore
	private ScheduleAction scheduleAction;

	public String getScheduleActionId() {
		return scheduleActionId;
	}

	public UpdateSchedulingAction setScheduleActionId(String scheduleActionId) {
		this.scheduleActionId = scheduleActionId;
		return this;
	}
	@JsonIgnore
	public ScheduleAction getScheduleAction() {
		return scheduleAction;
	}

	public UpdateSchedulingAction setScheduleAction(
			ScheduleAction scheduleAction) {
		this.scheduleAction = scheduleAction;
		return this;
	}
}
