package com.flexicore.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokerRequest;
import org.slf4j.Logger;

import java.util.Map;

public class ActionManagerContext {
	@JsonIgnore
	private Logger logger;
	private SecurityContextBase securityContext;
	private Scenario scenario;
	private Map<String, ExecuteInvokerRequest> actionMap;
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule()).configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	@JsonIgnore
	public Logger getLogger() {
		return logger;
	}

	public <T extends ActionManagerContext> T setLogger(Logger logger) {
		this.logger = logger;
		return (T) this;
	}

	public SecurityContextBase getSecurityContext() {
		return securityContext;
	}

	public <T extends ActionManagerContext> T setSecurityContext(
			SecurityContextBase securityContext) {
		this.securityContext = securityContext;
		return (T) this;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public <T extends ActionManagerContext> T setScenario(Scenario scenario) {
		this.scenario = scenario;
		return (T) this;
	}

	public Map<String, ExecuteInvokerRequest> getActionMap() {
		return actionMap;
	}

	public <T extends ActionManagerContext> T setActionMap(
			Map<String, ExecuteInvokerRequest> actionMap) {
		this.actionMap = actionMap;
		return (T) this;
	}


	public String toJson(Object o) throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}
}
