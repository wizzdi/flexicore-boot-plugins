package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTriggerType;

/** Object Used to Update ScenarioTriggerType */
public class ScenarioTriggerTypeUpdate extends ScenarioTriggerTypeCreate {

  private String id;

  @JsonIgnore private ScenarioTriggerType scenarioTriggerType;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return ScenarioTriggerTypeUpdate
   */
  public <T extends ScenarioTriggerTypeUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return scenarioTriggerType */
  @JsonIgnore
  public ScenarioTriggerType getScenarioTriggerType() {
    return this.scenarioTriggerType;
  }

  /**
   * @param scenarioTriggerType scenarioTriggerType to set
   * @return ScenarioTriggerTypeUpdate
   */
  public <T extends ScenarioTriggerTypeUpdate> T setScenarioTriggerType(
      ScenarioTriggerType scenarioTriggerType) {
    this.scenarioTriggerType = scenarioTriggerType;
    return (T) this;
  }
}
