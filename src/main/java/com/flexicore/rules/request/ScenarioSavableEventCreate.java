package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTrigger;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create ScenarioSavableEvent */
public class ScenarioSavableEventCreate extends BasicCreate {

  @JsonIgnore private ScenarioTrigger scenarioTrigger;

  private String scenarioTriggerId;

  /** @return scenarioTrigger */
  @JsonIgnore
  public ScenarioTrigger getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  /**
   * @param scenarioTrigger scenarioTrigger to set
   * @return ScenarioSavableEventCreate
   */
  public <T extends ScenarioSavableEventCreate> T setScenarioTrigger(
      ScenarioTrigger scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }

  /** @return scenarioTriggerId */
  public String getScenarioTriggerId() {
    return this.scenarioTriggerId;
  }

  /**
   * @param scenarioTriggerId scenarioTriggerId to set
   * @return ScenarioSavableEventCreate
   */
  public <T extends ScenarioSavableEventCreate> T setScenarioTriggerId(String scenarioTriggerId) {
    this.scenarioTriggerId = scenarioTriggerId;
    return (T) this;
  }
}
