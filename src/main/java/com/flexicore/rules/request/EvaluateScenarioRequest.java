package com.flexicore.rules.request;

import com.flexicore.rules.events.ScenarioEvent;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioTrigger;
import com.wizzdi.flexicore.boot.dynamic.invokers.request.ExecuteInvokerRequest;

import java.util.List;
import java.util.Map;

public class EvaluateScenarioRequest {
    private Scenario scenario;
    private List<ScenarioTrigger> scenarioTriggers;
    private List<DataSource> dataSources;
    private ScenarioEvent scenarioEvent;
    private Map<String, ExecuteInvokerRequest> actions;

    public Scenario getScenario() {
        return scenario;
    }

    public <T extends EvaluateScenarioRequest> T setScenario(Scenario scenario) {
        this.scenario = scenario;
        return (T) this;
    }

    public List<ScenarioTrigger> getScenarioTriggers() {
        return scenarioTriggers;
    }

    public <T extends EvaluateScenarioRequest> T setScenarioTriggers(List<ScenarioTrigger> scenarioTriggers) {
        this.scenarioTriggers = scenarioTriggers;
        return (T) this;
    }


    public List<DataSource> getDataSources() {
        return dataSources;
    }

    public <T extends EvaluateScenarioRequest> T setDataSources(List<DataSource> dataSources) {
        this.dataSources = dataSources;
        return (T) this;
    }

    public ScenarioEvent getScenarioEvent() {
        return scenarioEvent;
    }

    public <T extends EvaluateScenarioRequest> T setScenarioEvent(ScenarioEvent scenarioEvent) {
        this.scenarioEvent = scenarioEvent;
        return (T) this;
    }

    public Map<String, ExecuteInvokerRequest> getActions() {
        return actions;
    }

    public <T extends EvaluateScenarioRequest> T setActions(Map<String, ExecuteInvokerRequest> actions) {
        this.actions = actions;
        return (T) this;
    }
}

