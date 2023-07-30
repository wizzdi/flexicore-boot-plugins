package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTrigger;

/** Object Used to Update ScenarioTrigger */
public class ScenarioTriggerUpdate extends ScenarioTriggerCreate {

  private String id;

  @JsonIgnore private ScenarioTrigger scenarioTrigger;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return ScenarioTriggerUpdate
   */
  public <T extends ScenarioTriggerUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return scenarioTrigger */
  @JsonIgnore
  public ScenarioTrigger getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  /**
   * @param scenarioTrigger scenarioTrigger to set
   * @return ScenarioTriggerUpdate
   */
  public <T extends ScenarioTriggerUpdate> T setScenarioTrigger(ScenarioTrigger scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }
}
