package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.Scenario;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScenarioToDataSourceCreate extends BasicCreate {

  private Boolean enabled;

  private String dataSourceId;

  @JsonIgnore private DataSource dataSource;

  private Integer ordinal;

  private String scenarioId;

  @JsonIgnore private Scenario scenario;

  public Boolean isEnabled() {
    return this.enabled;
  }

  public <T extends ScenarioToDataSourceCreate> T setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  public String getDataSourceId() {
    return this.dataSourceId;
  }

  public <T extends ScenarioToDataSourceCreate> T setDataSourceId(String dataSourceId) {
    this.dataSourceId = dataSourceId;
    return (T) this;
  }

  @JsonIgnore
  public DataSource getDataSource() {
    return this.dataSource;
  }

  public <T extends ScenarioToDataSourceCreate> T setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    return (T) this;
  }

  public Integer getOrdinal() {
    return this.ordinal;
  }

  public <T extends ScenarioToDataSourceCreate> T setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  public String getScenarioId() {
    return this.scenarioId;
  }

  public <T extends ScenarioToDataSourceCreate> T setScenarioId(String scenarioId) {
    this.scenarioId = scenarioId;
    return (T) this;
  }

  @JsonIgnore
  public Scenario getScenario() {
    return this.scenario;
  }

  public <T extends ScenarioToDataSourceCreate> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }
}
