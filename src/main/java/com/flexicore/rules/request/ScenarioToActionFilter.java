package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class ScenarioToActionFilter extends PaginationFilter {

  private Set<Boolean> enabled;

  @JsonIgnore private List<ScenarioAction> scenarioAction;

  private Set<String> scenarioIds;

  @JsonIgnore private List<Scenario> scenario;

  private Set<String> scenarioActionIds;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<Boolean> getEnabled() {
    return this.enabled;
  }

  public <T extends ScenarioToActionFilter> T setEnabled(Set<Boolean> enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  @JsonIgnore
  public List<ScenarioAction> getScenarioAction() {
    return this.scenarioAction;
  }

  public <T extends ScenarioToActionFilter> T setScenarioAction(
      List<ScenarioAction> scenarioAction) {
    this.scenarioAction = scenarioAction;
    return (T) this;
  }

  public Set<String> getScenarioIds() {
    return this.scenarioIds;
  }

  public <T extends ScenarioToActionFilter> T setScenarioIds(Set<String> scenarioIds) {
    this.scenarioIds = scenarioIds;
    return (T) this;
  }

  @JsonIgnore
  public List<Scenario> getScenario() {
    return this.scenario;
  }

  public <T extends ScenarioToActionFilter> T setScenario(List<Scenario> scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  public Set<String> getScenarioActionIds() {
    return this.scenarioActionIds;
  }

  public <T extends ScenarioToActionFilter> T setScenarioActionIds(Set<String> scenarioActionIds) {
    this.scenarioActionIds = scenarioActionIds;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScenarioToActionFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
