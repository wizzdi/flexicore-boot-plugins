package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioAction;

public class ScenarioActionUpdate extends ScenarioActionCreate {

  private String id;
  @JsonIgnore private ScenarioAction scenarioAction;

  public String getId() {
    return id;
  }

  public <T extends ScenarioActionUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioAction getScenarioAction() {
    return scenarioAction;
  }

  public <T extends ScenarioActionUpdate> T setScenarioAction(ScenarioAction scenarioAction) {
    this.scenarioAction = scenarioAction;
    return (T) this;
  }
}
