package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioToDataSource;

/** Object Used to Update ScenarioToDataSource */
public class ScenarioToDataSourceUpdate extends ScenarioToDataSourceCreate {

  private String id;

  @JsonIgnore private ScenarioToDataSource scenarioToDataSource;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return ScenarioToDataSourceUpdate
   */
  public <T extends ScenarioToDataSourceUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return scenarioToDataSource */
  @JsonIgnore
  public ScenarioToDataSource getScenarioToDataSource() {
    return this.scenarioToDataSource;
  }

  /**
   * @param scenarioToDataSource scenarioToDataSource to set
   * @return ScenarioToDataSourceUpdate
   */
  public <T extends ScenarioToDataSourceUpdate> T setScenarioToDataSource(
      ScenarioToDataSource scenarioToDataSource) {
    this.scenarioToDataSource = scenarioToDataSource;
    return (T) this;
  }
}
