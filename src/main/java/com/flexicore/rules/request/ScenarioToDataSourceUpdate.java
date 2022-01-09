package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioToDataSource;

public class ScenarioToDataSourceUpdate extends ScenarioToDataSourceCreate {

  private String id;
  @JsonIgnore private ScenarioToDataSource scenarioToDataSource;

  public String getId() {
    return id;
  }

  public <T extends ScenarioToDataSourceUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioToDataSource getScenarioToDataSource() {
    return scenarioToDataSource;
  }

  public <T extends ScenarioToDataSourceUpdate> T setScenarioToDataSource(
      ScenarioToDataSource scenarioToDataSource) {
    this.scenarioToDataSource = scenarioToDataSource;
    return (T) this;
  }
}
