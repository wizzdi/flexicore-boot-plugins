package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.Scenario;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create ScenarioToDataSource */
public class ScenarioToDataSourceCreate extends BasicCreate {

  private String dataSourceId;

  @JsonIgnore private Scenario scenario;

  @JsonIgnore private DataSource dataSource;

  private String scenarioId;

  private Integer ordinal;

  private Boolean enabled;

  /** @return dataSourceId */
  public String getDataSourceId() {
    return this.dataSourceId;
  }

  /**
   * @param dataSourceId dataSourceId to set
   * @return ScenarioToDataSourceCreate
   */
  public <T extends ScenarioToDataSourceCreate> T setDataSourceId(String dataSourceId) {
    this.dataSourceId = dataSourceId;
    return (T) this;
  }

  /** @return scenario */
  @JsonIgnore
  public Scenario getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioToDataSourceCreate
   */
  public <T extends ScenarioToDataSourceCreate> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  /** @return dataSource */
  @JsonIgnore
  public DataSource getDataSource() {
    return this.dataSource;
  }

  /**
   * @param dataSource dataSource to set
   * @return ScenarioToDataSourceCreate
   */
  public <T extends ScenarioToDataSourceCreate> T setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    return (T) this;
  }

  /** @return scenarioId */
  public String getScenarioId() {
    return this.scenarioId;
  }

  /**
   * @param scenarioId scenarioId to set
   * @return ScenarioToDataSourceCreate
   */
  public <T extends ScenarioToDataSourceCreate> T setScenarioId(String scenarioId) {
    this.scenarioId = scenarioId;
    return (T) this;
  }

  /** @return ordinal */
  public Integer getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return ScenarioToDataSourceCreate
   */
  public <T extends ScenarioToDataSourceCreate> T setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  /** @return enabled */
  public Boolean getEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToDataSourceCreate
   */
  public <T extends ScenarioToDataSourceCreate> T setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }
}
