package com.flexicore.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;

public class TriggerManagerContext {
	@JsonIgnore
	private Logger logger;
	private Scenario scenario;
	private ScenarioTrigger scenarioTrigger;
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule()).configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@JsonIgnore
	public Logger getLogger() {
		return logger;
	}

	public <T extends TriggerManagerContext> T setLogger(Logger logger) {
		this.logger = logger;
		return (T) this;
	}


	public String toJson(Object o) throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}

	public Scenario getScenario() {
		return scenario;
	}

	public <T extends TriggerManagerContext> T setScenario(Scenario scenario) {
		this.scenario = scenario;
		return (T) this;
	}

	public ScenarioTrigger getScenarioTrigger() {
		return scenarioTrigger;
	}

	public <T extends TriggerManagerContext> T setScenarioTrigger(
			ScenarioTrigger scenarioTrigger) {
		this.scenarioTrigger = scenarioTrigger;
		return (T) this;
	}
}
