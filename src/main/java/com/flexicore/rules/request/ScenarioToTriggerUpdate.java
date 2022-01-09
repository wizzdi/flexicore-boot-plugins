package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioToTrigger;

public class ScenarioToTriggerUpdate extends ScenarioToTriggerCreate {

  private String id;
  @JsonIgnore private ScenarioToTrigger scenarioToTrigger;

  public String getId() {
    return id;
  }

  public <T extends ScenarioToTriggerUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioToTrigger getScenarioToTrigger() {
    return scenarioToTrigger;
  }

  public <T extends ScenarioToTriggerUpdate> T setScenarioToTrigger(
      ScenarioToTrigger scenarioToTrigger) {
    this.scenarioToTrigger = scenarioToTrigger;
    return (T) this;
  }
}
