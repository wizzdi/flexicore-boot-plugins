package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioToTrigger;

/** Object Used to Update ScenarioToTrigger */
public class ScenarioToTriggerUpdate extends ScenarioToTriggerCreate {

  private String id;

  @JsonIgnore private ScenarioToTrigger scenarioToTrigger;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return ScenarioToTriggerUpdate
   */
  public <T extends ScenarioToTriggerUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return scenarioToTrigger */
  @JsonIgnore
  public ScenarioToTrigger getScenarioToTrigger() {
    return this.scenarioToTrigger;
  }

  /**
   * @param scenarioToTrigger scenarioToTrigger to set
   * @return ScenarioToTriggerUpdate
   */
  public <T extends ScenarioToTriggerUpdate> T setScenarioToTrigger(
      ScenarioToTrigger scenarioToTrigger) {
    this.scenarioToTrigger = scenarioToTrigger;
    return (T) this;
  }
}
