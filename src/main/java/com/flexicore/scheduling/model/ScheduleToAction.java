package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Baselink;
import com.flexicore.security.SecurityContext;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ScheduleToAction extends Baselink {

	public ScheduleToAction() {
	}

	public ScheduleToAction(String name, SecurityContext securityContext) {
		super(name, securityContext);
	}

	@Override
	@ManyToOne(targetEntity = Schedule.class, cascade = {CascadeType.MERGE,
			CascadeType.PERSIST})
	public Schedule getLeftside() {
		return (Schedule) super.getLeftside();
	}

	@JsonIgnore
	public void setLeftside(Schedule leftside) {
		super.setLeftside(leftside);
	}

	@Override
	public void setLeftside(Baseclass leftside) {
		super.setLeftside(leftside);
	}

	@Override
	@ManyToOne(targetEntity = ScheduleAction.class, cascade = {
			CascadeType.MERGE, CascadeType.PERSIST})
	public ScheduleAction getRightside() {
		return (ScheduleAction) super.getRightside();
	}

	@JsonIgnore
	public void setRightside(ScheduleAction rightside) {
		super.setRightside(rightside);
	}

	@Override
	public void setRightside(Baseclass rightside) {
		super.setRightside(rightside);
	}
}
