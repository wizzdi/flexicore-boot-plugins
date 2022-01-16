package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioAction;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

/** Object Used to List ScenarioToAction */
public class ScenarioToActionFilter extends PaginationFilter {

  @JsonIgnore private List<ScenarioAction> scenarioAction;

  private Set<Boolean> enabled;

  private Set<String> scenarioIds;

  @JsonIgnore private List<Scenario> scenario;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> scenarioActionIds;

  /** @return scenarioAction */
  @JsonIgnore
  public List<ScenarioAction> getScenarioAction() {
    return this.scenarioAction;
  }

  /**
   * @param scenarioAction scenarioAction to set
   * @return ScenarioToActionFilter
   */
  public <T extends ScenarioToActionFilter> T setScenarioAction(
      List<ScenarioAction> scenarioAction) {
    this.scenarioAction = scenarioAction;
    return (T) this;
  }

  /** @return enabled */
  public Set<Boolean> getEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToActionFilter
   */
  public <T extends ScenarioToActionFilter> T setEnabled(Set<Boolean> enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  /** @return scenarioIds */
  public Set<String> getScenarioIds() {
    return this.scenarioIds;
  }

  /**
   * @param scenarioIds scenarioIds to set
   * @return ScenarioToActionFilter
   */
  public <T extends ScenarioToActionFilter> T setScenarioIds(Set<String> scenarioIds) {
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
   * @return ScenarioToActionFilter
   */
  public <T extends ScenarioToActionFilter> T setScenario(List<Scenario> scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return ScenarioToActionFilter
   */
  public <T extends ScenarioToActionFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return scenarioActionIds */
  public Set<String> getScenarioActionIds() {
    return this.scenarioActionIds;
  }

  /**
   * @param scenarioActionIds scenarioActionIds to set
   * @return ScenarioToActionFilter
   */
  public <T extends ScenarioToActionFilter> T setScenarioActionIds(Set<String> scenarioActionIds) {
    this.scenarioActionIds = scenarioActionIds;
    return (T) this;
  }
}
