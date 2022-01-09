package com.flexicore.rules.request;

import com.flexicore.rules.events.ScenarioEvent;
import com.flexicore.rules.model.ScenarioTrigger;

public class EvaluateTriggerRequest {
    private ScenarioEvent scenarioEvent;
    private ScenarioTrigger scenarioTrigger;

    public ScenarioEvent getScenarioEvent() {
        return scenarioEvent;
    }

    public <T extends EvaluateTriggerRequest> T setScenarioEvent(ScenarioEvent scenarioEvent) {
        this.scenarioEvent = scenarioEvent;
        return (T) this;
    }

    public ScenarioTrigger getScenarioTrigger() {
        return scenarioTrigger;
    }

    public <T extends EvaluateTriggerRequest> T setScenarioTrigger(ScenarioTrigger scenarioTrigger) {
        this.scenarioTrigger = scenarioTrigger;
        return (T) this;
    }
}
