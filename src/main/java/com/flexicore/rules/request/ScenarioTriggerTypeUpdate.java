package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTriggerType;

public class ScenarioTriggerTypeUpdate extends ScenarioTriggerTypeCreate {

  private String id;
  @JsonIgnore private ScenarioTriggerType scenarioTriggerType;

  public String getId() {
    return id;
  }

  public <T extends ScenarioTriggerTypeUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioTriggerType getScenarioTriggerType() {
    return scenarioTriggerType;
  }

  public <T extends ScenarioTriggerTypeUpdate> T setScenarioTriggerType(
      ScenarioTriggerType scenarioTriggerType) {
    this.scenarioTriggerType = scenarioTriggerType;
    return (T) this;
  }
}
