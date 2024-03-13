package com.flexicore.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flexicore.rules.events.ScenarioEvent;
import com.flexicore.security.SecurityContextBase;
import org.slf4j.Logger;


public class EvaluateTriggerScriptContext {
	@JsonIgnore
	private Logger logger;
	private SecurityContextBase securityContext;
	private ScenarioEvent scenarioEvent;
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule()).configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@JsonIgnore
	public Logger getLogger() {
		return logger;
	}

	public <T extends EvaluateTriggerScriptContext> T setLogger(Logger logger) {
		this.logger = logger;
		return (T) this;
	}

	public SecurityContextBase getSecurityContext() {
		return securityContext;
	}

	public <T extends EvaluateTriggerScriptContext> T setSecurityContext(
			SecurityContextBase securityContext) {
		this.securityContext = securityContext;
		return (T) this;
	}

	public ScenarioEvent getScenarioEvent() {
		return scenarioEvent;
	}

	public <T extends EvaluateTriggerScriptContext> T setScenarioEvent(ScenarioEvent scenarioEvent) {
		this.scenarioEvent = scenarioEvent;
		return (T) this;
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public String toJson(Object o) throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}
}
