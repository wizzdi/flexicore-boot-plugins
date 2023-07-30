package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioAction;

/** Object Used to Update ScenarioAction */
public class ScenarioActionUpdate extends ScenarioActionCreate {

  @JsonIgnore private ScenarioAction scenarioAction;

  private String id;

  /** @return scenarioAction */
  @JsonIgnore
  public ScenarioAction getScenarioAction() {
    return this.scenarioAction;
  }

  /**
   * @param scenarioAction scenarioAction to set
   * @return ScenarioActionUpdate
   */
  public <T extends ScenarioActionUpdate> T setScenarioAction(ScenarioAction scenarioAction) {
    this.scenarioAction = scenarioAction;
    return (T) this;
  }

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return ScenarioActionUpdate
   */
  public <T extends ScenarioActionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }
}
