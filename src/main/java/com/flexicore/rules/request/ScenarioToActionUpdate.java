package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioToAction;

public class ScenarioToActionUpdate extends ScenarioToActionCreate {

  private String id;
  @JsonIgnore private ScenarioToAction scenarioToAction;

  public String getId() {
    return id;
  }

  public <T extends ScenarioToActionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioToAction getScenarioToAction() {
    return scenarioToAction;
  }

  public <T extends ScenarioToActionUpdate> T setScenarioToAction(
      ScenarioToAction scenarioToAction) {
    this.scenarioToAction = scenarioToAction;
    return (T) this;
  }
}
