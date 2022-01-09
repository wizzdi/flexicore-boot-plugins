package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScenarioToActionCreate extends BasicCreate {

  private Boolean enabled;

  @JsonIgnore private ScenarioAction scenarioAction;

  private String scenarioId;

  @JsonIgnore private Scenario scenario;

  private String scenarioActionId;

  public Boolean isEnabled() {
    return this.enabled;
  }

  public <T extends ScenarioToActionCreate> T setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioAction getScenarioAction() {
    return this.scenarioAction;
  }

  public <T extends ScenarioToActionCreate> T setScenarioAction(ScenarioAction scenarioAction) {
    this.scenarioAction = scenarioAction;
    return (T) this;
  }

  public String getScenarioId() {
    return this.scenarioId;
  }

  public <T extends ScenarioToActionCreate> T setScenarioId(String scenarioId) {
    this.scenarioId = scenarioId;
    return (T) this;
  }

  @JsonIgnore
  public Scenario getScenario() {
    return this.scenario;
  }

  public <T extends ScenarioToActionCreate> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  public String getScenarioActionId() {
    return this.scenarioActionId;
  }

  public <T extends ScenarioToActionCreate> T setScenarioActionId(String scenarioActionId) {
    this.scenarioActionId = scenarioActionId;
    return (T) this;
  }
}
