package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create ScenarioToAction */
public class ScenarioToActionCreate extends BasicCreate {

  private String scenarioActionId;

  private String scenarioId;

  private Boolean enabled;

  @JsonIgnore private ScenarioAction scenarioAction;

  @JsonIgnore private Scenario scenario;

  /** @return scenarioActionId */
  public String getScenarioActionId() {
    return this.scenarioActionId;
  }

  /**
   * @param scenarioActionId scenarioActionId to set
   * @return ScenarioToActionCreate
   */
  public <T extends ScenarioToActionCreate> T setScenarioActionId(String scenarioActionId) {
    this.scenarioActionId = scenarioActionId;
    return (T) this;
  }

  /** @return scenarioId */
  public String getScenarioId() {
    return this.scenarioId;
  }

  /**
   * @param scenarioId scenarioId to set
   * @return ScenarioToActionCreate
   */
  public <T extends ScenarioToActionCreate> T setScenarioId(String scenarioId) {
    this.scenarioId = scenarioId;
    return (T) this;
  }

  /** @return enabled */
  public Boolean getEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToActionCreate
   */
  public <T extends ScenarioToActionCreate> T setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  /** @return scenarioAction */
  @JsonIgnore
  public ScenarioAction getScenarioAction() {
    return this.scenarioAction;
  }

  /**
   * @param scenarioAction scenarioAction to set
   * @return ScenarioToActionCreate
   */
  public <T extends ScenarioToActionCreate> T setScenarioAction(ScenarioAction scenarioAction) {
    this.scenarioAction = scenarioAction;
    return (T) this;
  }

  /** @return scenario */
  @JsonIgnore
  public Scenario getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioToActionCreate
   */
  public <T extends ScenarioToActionCreate> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }
}
