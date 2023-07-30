package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioTrigger;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create ScenarioToTrigger */
public class ScenarioToTriggerCreate extends BasicCreate {

  private String scenarioTriggerId;

  @JsonIgnore private ScenarioTrigger scenarioTrigger;

  private String scenarioId;

  @JsonIgnore private Scenario scenario;

  private Boolean enabled;

  private Integer ordinal;

  private Boolean firing;

  /** @return scenarioTriggerId */
  public String getScenarioTriggerId() {
    return this.scenarioTriggerId;
  }

  /**
   * @param scenarioTriggerId scenarioTriggerId to set
   * @return ScenarioToTriggerCreate
   */
  public <T extends ScenarioToTriggerCreate> T setScenarioTriggerId(String scenarioTriggerId) {
    this.scenarioTriggerId = scenarioTriggerId;
    return (T) this;
  }

  /** @return scenarioTrigger */
  @JsonIgnore
  public ScenarioTrigger getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  /**
   * @param scenarioTrigger scenarioTrigger to set
   * @return ScenarioToTriggerCreate
   */
  public <T extends ScenarioToTriggerCreate> T setScenarioTrigger(ScenarioTrigger scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }

  /** @return scenarioId */
  public String getScenarioId() {
    return this.scenarioId;
  }

  /**
   * @param scenarioId scenarioId to set
   * @return ScenarioToTriggerCreate
   */
  public <T extends ScenarioToTriggerCreate> T setScenarioId(String scenarioId) {
    this.scenarioId = scenarioId;
    return (T) this;
  }

  /** @return scenario */
  @JsonIgnore
  public Scenario getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioToTriggerCreate
   */
  public <T extends ScenarioToTriggerCreate> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  /** @return enabled */
  public Boolean getEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToTriggerCreate
   */
  public <T extends ScenarioToTriggerCreate> T setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  /** @return ordinal */
  public Integer getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return ScenarioToTriggerCreate
   */
  public <T extends ScenarioToTriggerCreate> T setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  /** @return firing */
  public Boolean getFiring() {
    return this.firing;
  }

  /**
   * @param firing firing to set
   * @return ScenarioToTriggerCreate
   */
  public <T extends ScenarioToTriggerCreate> T setFiring(Boolean firing) {
    this.firing = firing;
    return (T) this;
  }
}
