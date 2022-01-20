package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioSavableEvent;

/** Object Used to Update ScenarioSavableEvent */
public class ScenarioSavableEventUpdate extends ScenarioSavableEventCreate {

  @JsonIgnore private ScenarioSavableEvent scenarioSavableEvent;

  private String id;

  /** @return scenarioSavableEvent */
  @JsonIgnore
  public ScenarioSavableEvent getScenarioSavableEvent() {
    return this.scenarioSavableEvent;
  }

  /**
   * @param scenarioSavableEvent scenarioSavableEvent to set
   * @return ScenarioSavableEventUpdate
   */
  public <T extends ScenarioSavableEventUpdate> T setScenarioSavableEvent(
      ScenarioSavableEvent scenarioSavableEvent) {
    this.scenarioSavableEvent = scenarioSavableEvent;
    return (T) this;
  }

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return ScenarioSavableEventUpdate
   */
  public <T extends ScenarioSavableEventUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }
}
