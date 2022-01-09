package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTrigger;

public class ScenarioTriggerUpdate extends ScenarioTriggerCreate {

  private String id;
  @JsonIgnore private ScenarioTrigger scenarioTrigger;

  public String getId() {
    return id;
  }

  public <T extends ScenarioTriggerUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioTrigger getScenarioTrigger() {
    return scenarioTrigger;
  }

  public <T extends ScenarioTriggerUpdate> T setScenarioTrigger(ScenarioTrigger scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }
}
