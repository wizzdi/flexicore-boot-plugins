package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;

public class ClearLogRequest {

	private String scenarioId;
	@JsonIgnore
	private Scenario scenario;

	public String getScenarioId() {
		return scenarioId;
	}

	public <T extends ClearLogRequest> T setScenarioId(String scenarioId) {
		this.scenarioId = scenarioId;
		return (T) this;
	}

	@JsonIgnore
	public Scenario getScenario() {
		return scenario;
	}

	public <T extends ClearLogRequest> T setScenario(Scenario scenario) {
		this.scenario = scenario;
		return (T) this;
	}
}
