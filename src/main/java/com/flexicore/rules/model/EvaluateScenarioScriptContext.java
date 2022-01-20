package com.flexicore.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.flexicore.rules.events.ScenarioEvent;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokerRequest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

public class EvaluateScenarioScriptContext {
	@JsonIgnore
	private Logger logger;
	private SecurityContextBase securityContext;
	private List<ScenarioTrigger> scenarioTriggers;
	private List<DataSource> scenarioToDataSources;
	private ScenarioEvent scenarioEvent;
	private Scenario scenario;
	private Map<String, ExecuteInvokerRequest> actions;

	private Map<String,ScenarioSavableEvent> eventCache=new ConcurrentHashMap<>();
	private Function<String, ScenarioSavableEvent> fetchEvent;
	private static final ObjectMapper objectMapper = new ObjectMapper()
			.registerModule(new JavaTimeModule()).configure(
					DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	public EvaluateScenarioScriptContext(Function<String, ScenarioSavableEvent> fetchEvent) {
		this.fetchEvent = fetchEvent;
	}

	@JsonIgnore
	public Logger getLogger() {
		return logger;
	}

	public <T extends EvaluateScenarioScriptContext> T setLogger(Logger logger) {
		this.logger = logger;
		return (T) this;
	}

	public SecurityContextBase getSecurityContext() {
		return securityContext;
	}

	public <T extends EvaluateScenarioScriptContext> T setSecurityContext(
			SecurityContextBase securityContext) {
		this.securityContext = securityContext;
		return (T) this;
	}

	public ScenarioEvent getScenarioEvent() {
		return scenarioEvent;
	}

	public <T extends EvaluateScenarioScriptContext> T setScenarioEvent(ScenarioEvent scenarioEvent) {
		this.scenarioEvent = scenarioEvent;
		return (T) this;
	}

	public static ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public String toJson(Object o) throws JsonProcessingException {
		return objectMapper.writeValueAsString(o);
	}

	public ScenarioSavableEvent getEvent(ScenarioTrigger scenarioTrigger){
		return eventCache.computeIfAbsent(scenarioTrigger.getId(),f->fetchEvent.apply(scenarioTrigger.getLastEventId()));
	}

	public List<ScenarioTrigger> getScenarioTriggers() {
		return scenarioTriggers;
	}

	public <T extends EvaluateScenarioScriptContext> T setScenarioTriggers(List<ScenarioTrigger> scenarioTriggers) {
		this.scenarioTriggers = scenarioTriggers;
		return (T) this;
	}

	public List<DataSource> getScenarioToDataSources() {
		return scenarioToDataSources;
	}

	public <T extends EvaluateScenarioScriptContext> T setScenarioToDataSources(List<DataSource> scenarioToDataSources) {
		this.scenarioToDataSources = scenarioToDataSources;
		return (T) this;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public <T extends EvaluateScenarioScriptContext> T setScenario(Scenario scenario) {
		this.scenario = scenario;
		return (T) this;
	}

	public Map<String, ExecuteInvokerRequest> getActions() {
		return actions;
	}

	public <T extends EvaluateScenarioScriptContext> T setActions(Map<String, ExecuteInvokerRequest> actions) {
		this.actions = actions;
		return (T) this;
	}
}
