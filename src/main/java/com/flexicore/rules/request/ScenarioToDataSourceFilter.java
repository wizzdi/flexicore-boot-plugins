package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.Scenario;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

/** Object Used to List ScenarioToDataSource */
public class ScenarioToDataSourceFilter extends PaginationFilter {

  private Set<Integer> ordinal;

  private Set<String> scenarioIds;

  @JsonIgnore private List<Scenario> scenario;

  private Set<Boolean> enabled;

  @JsonIgnore private List<DataSource> dataSource;

  private Set<String> dataSourceIds;

  /** @return ordinal */
  public Set<Integer> getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return ScenarioToDataSourceFilter
   */
  public <T extends ScenarioToDataSourceFilter> T setOrdinal(Set<Integer> ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  /** @return scenarioIds */
  public Set<String> getScenarioIds() {
    return this.scenarioIds;
  }

  /**
   * @param scenarioIds scenarioIds to set
   * @return ScenarioToDataSourceFilter
   */
  public <T extends ScenarioToDataSourceFilter> T setScenarioIds(Set<String> scenarioIds) {
    this.scenarioIds = scenarioIds;
    return (T) this;
  }

  /** @return scenario */
  @JsonIgnore
  public List<Scenario> getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioToDataSourceFilter
   */
  public <T extends ScenarioToDataSourceFilter> T setScenario(List<Scenario> scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  /** @return enabled */
  public Set<Boolean> getEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToDataSourceFilter
   */
  public <T extends ScenarioToDataSourceFilter> T setEnabled(Set<Boolean> enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  /** @return dataSource */
  @JsonIgnore
  public List<DataSource> getDataSource() {
    return this.dataSource;
  }

  /**
   * @param dataSource dataSource to set
   * @return ScenarioToDataSourceFilter
   */
  public <T extends ScenarioToDataSourceFilter> T setDataSource(List<DataSource> dataSource) {
    this.dataSource = dataSource;
    return (T) this;
  }

  /** @return dataSourceIds */
  public Set<String> getDataSourceIds() {
    return this.dataSourceIds;
  }

  /**
   * @param dataSourceIds dataSourceIds to set
   * @return ScenarioToDataSourceFilter
   */
  public <T extends ScenarioToDataSourceFilter> T setDataSourceIds(Set<String> dataSourceIds) {
    this.dataSourceIds = dataSourceIds;
    return (T) this;
  }
}
