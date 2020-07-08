package com.flexicore.scheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.Baseclass;
import com.flexicore.model.dynamic.DynamicExecution;
import com.flexicore.model.dynamic.ExecutionParametersHolder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ScheduleAction extends Baseclass {

	@ManyToOne(targetEntity = DynamicExecution.class)
	private DynamicExecution dynamicExecution;

	@JsonIgnore
	@OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "rightside", cascade = {
			CascadeType.PERSIST, CascadeType.MERGE})
	private List<ScheduleToAction> scheduleToActions = new ArrayList<>();

	@JsonIgnore
	@OneToMany(targetEntity = ScheduleToAction.class, mappedBy = "rightside", cascade = {
			CascadeType.PERSIST, CascadeType.MERGE})
	public List<ScheduleToAction> getScheduleToActions() {
		return scheduleToActions;
	}

	public ScheduleAction setScheduleToActions(
			List<ScheduleToAction> scheduleToActions) {
		this.scheduleToActions = scheduleToActions;
		return this;
	}

	@ManyToOne(targetEntity = DynamicExecution.class)
	public DynamicExecution getDynamicExecution() {
		return dynamicExecution;
	}

	public <T extends ScheduleAction> T setDynamicExecution(
			DynamicExecution dynamicExecution) {
		this.dynamicExecution = dynamicExecution;
		return (T) this;
	}
}
