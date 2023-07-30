package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioToAction;

/** Object Used to Update ScenarioToAction */
public class ScenarioToActionUpdate extends ScenarioToActionCreate {

  @JsonIgnore private ScenarioToAction scenarioToAction;

  private String id;

  /** @return scenarioToAction */
  @JsonIgnore
  public ScenarioToAction getScenarioToAction() {
    return this.scenarioToAction;
  }

  /**
   * @param scenarioToAction scenarioToAction to set
   * @return ScenarioToActionUpdate
   */
  public <T extends ScenarioToActionUpdate> T setScenarioToAction(
      ScenarioToAction scenarioToAction) {
    this.scenarioToAction = scenarioToAction;
    return (T) this;
  }

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return ScenarioToActionUpdate
   */
  public <T extends ScenarioToActionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }
}
