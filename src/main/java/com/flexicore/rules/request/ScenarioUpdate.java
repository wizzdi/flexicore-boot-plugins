package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;

/** Object Used to Update Scenario */
public class ScenarioUpdate extends ScenarioCreate {

  private String id;

  @JsonIgnore private Scenario scenario;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return ScenarioUpdate
   */
  public <T extends ScenarioUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return scenario */
  @JsonIgnore
  public Scenario getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioUpdate
   */
  public <T extends ScenarioUpdate> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }
}
