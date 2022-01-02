package com.flexicore.scheduling.model;

import com.flexicore.model.SecuredBasic;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ScheduleToAction extends SecuredBasic {

	@ManyToOne(targetEntity = Schedule.class)
	private Schedule schedule;
	@ManyToOne(targetEntity = ScheduleAction.class)
	private ScheduleAction scheduleAction;

	@ManyToOne(targetEntity = Schedule.class)
	public Schedule getSchedule() {
		return schedule;
	}

	public <T extends ScheduleToAction> T setSchedule(Schedule schedule) {
		this.schedule = schedule;
		return (T) this;
	}

	@ManyToOne(targetEntity = ScheduleAction.class)
	public ScheduleAction getScheduleAction() {
		return scheduleAction;
	}

	public <T extends ScheduleToAction> T setScheduleAction(ScheduleAction scheduleAction) {
		this.scheduleAction = scheduleAction;
		return (T) this;
	}
}
