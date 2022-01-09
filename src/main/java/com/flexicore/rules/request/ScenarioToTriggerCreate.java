package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioTrigger;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScenarioToTriggerCreate extends BasicCreate {

  private Boolean firing;

  @JsonIgnore private ScenarioTrigger scenarioTrigger;

  private String scenarioId;

  private String scenarioTriggerId;

  @JsonIgnore private Scenario scenario;

  private Integer ordinal;

  private Boolean enabled;

  public Boolean isFiring() {
    return this.firing;
  }

  public <T extends ScenarioToTriggerCreate> T setFiring(Boolean firing) {
    this.firing = firing;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioTrigger getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  public <T extends ScenarioToTriggerCreate> T setScenarioTrigger(ScenarioTrigger scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }

  public String getScenarioId() {
    return this.scenarioId;
  }

  public <T extends ScenarioToTriggerCreate> T setScenarioId(String scenarioId) {
    this.scenarioId = scenarioId;
    return (T) this;
  }

  public String getScenarioTriggerId() {
    return this.scenarioTriggerId;
  }

  public <T extends ScenarioToTriggerCreate> T setScenarioTriggerId(String scenarioTriggerId) {
    this.scenarioTriggerId = scenarioTriggerId;
    return (T) this;
  }

  @JsonIgnore
  public Scenario getScenario() {
    return this.scenario;
  }

  public <T extends ScenarioToTriggerCreate> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  public Integer getOrdinal() {
    return this.ordinal;
  }

  public <T extends ScenarioToTriggerCreate> T setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  public Boolean isEnabled() {
    return this.enabled;
  }

  public <T extends ScenarioToTriggerCreate> T setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }
}
