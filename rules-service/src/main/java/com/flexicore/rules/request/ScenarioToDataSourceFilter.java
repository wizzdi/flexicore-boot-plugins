package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.Scenario;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;

import java.util.List;
import java.util.Set;

public class ScenarioToDataSourceFilter extends PaginationFilter {

  private Boolean enabled;

  private Set<String> dataSourceIds;

  @JsonIgnore
  private List<DataSource> dataSource;

  private Set<Integer> ordinal;

  private Set<String> scenarioIds;

  private BasicPropertiesFilter basicPropertiesFilter;

  @JsonIgnore
  private List<Scenario> scenario;

  public Boolean getEnabled() {
    return enabled;
  }

  public <T extends ScenarioToDataSourceFilter> T setEnabled(Boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  public Set<String> getDataSourceIds() {
    return this.dataSourceIds;
  }

  public <T extends ScenarioToDataSourceFilter> T setDataSourceIds(Set<String> dataSourceIds) {
    this.dataSourceIds = dataSourceIds;
    return (T) this;
  }

  @JsonIgnore
  public List<DataSource> getDataSource() {
    return this.dataSource;
  }

  public <T extends ScenarioToDataSourceFilter> T setDataSource(List<DataSource> dataSource) {
    this.dataSource = dataSource;
    return (T) this;
  }

  public Set<Integer> getOrdinal() {
    return this.ordinal;
  }

  public <T extends ScenarioToDataSourceFilter> T setOrdinal(Set<Integer> ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  public Set<String> getScenarioIds() {
    return this.scenarioIds;
  }

  public <T extends ScenarioToDataSourceFilter> T setScenarioIds(Set<String> scenarioIds) {
    this.scenarioIds = scenarioIds;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScenarioToDataSourceFilter> T setBasicPropertiesFilter(
          BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  @JsonIgnore
  public List<Scenario> getScenario() {
    return this.scenario;
  }

  public <T extends ScenarioToDataSourceFilter> T setScenario(List<Scenario> scenario) {
    this.scenario = scenario;
    return (T) this;
  }
}
