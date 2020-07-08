package com.flexicore.scheduling.containers.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.dynamic.DynamicExecution;

public class CreateSchedulingAction {

	private String name;
	private String description;
	private String dynamicExecutionId;
	@JsonIgnore
	private DynamicExecution dynamicExecution;

	public String getName() {
		return name;
	}

	public <T extends CreateSchedulingAction> T setName(String name) {
		this.name = name;
		return (T) this;
	}

	public String getDescription() {
		return description;
	}

	public <T extends CreateSchedulingAction> T setDescription(
			String description) {
		this.description = description;
		return (T) this;
	}

	public String getDynamicExecutionId() {
		return dynamicExecutionId;
	}

	public <T extends CreateSchedulingAction> T setDynamicExecutionId(
			String dynamicExecutionId) {
		this.dynamicExecutionId = dynamicExecutionId;
		return (T) this;
	}

	@JsonIgnore
	public DynamicExecution getDynamicExecution() {
		return dynamicExecution;
	}

	public <T extends CreateSchedulingAction> T setDynamicExecution(
			DynamicExecution dynamicExecution) {
		this.dynamicExecution = dynamicExecution;
		return (T) this;
	}
}
