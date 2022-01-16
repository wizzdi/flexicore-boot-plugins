package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioTrigger;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

/** Object Used to List ScenarioToTrigger */
public class ScenarioToTriggerFilter extends PaginationFilter {

  private Set<Boolean> firing;

  private Set<Boolean> enabled;

  private Set<Integer> ordinal;

  @JsonIgnore private List<Scenario> scenario;

  private Set<String> scenarioTriggerIds;

  @JsonIgnore private List<ScenarioTrigger> scenarioTrigger;

  private Set<String> scenarioIds;

  private BasicPropertiesFilter basicPropertiesFilter;

  /** @return firing */
  public Set<Boolean> getFiring() {
    return this.firing;
  }

  /**
   * @param firing firing to set
   * @return ScenarioToTriggerFilter
   */
  public <T extends ScenarioToTriggerFilter> T setFiring(Set<Boolean> firing) {
    this.firing = firing;
    return (T) this;
  }

  /** @return enabled */
  public Set<Boolean> getEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToTriggerFilter
   */
  public <T extends ScenarioToTriggerFilter> T setEnabled(Set<Boolean> enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  /** @return ordinal */
  public Set<Integer> getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return ScenarioToTriggerFilter
   */
  public <T extends ScenarioToTriggerFilter> T setOrdinal(Set<Integer> ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  /** @return scenario */
  @JsonIgnore
  public List<Scenario> getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioToTriggerFilter
   */
  public <T extends ScenarioToTriggerFilter> T setScenario(List<Scenario> scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  /** @return scenarioTriggerIds */
  public Set<String> getScenarioTriggerIds() {
    return this.scenarioTriggerIds;
  }

  /**
   * @param scenarioTriggerIds scenarioTriggerIds to set
   * @return ScenarioToTriggerFilter
   */
  public <T extends ScenarioToTriggerFilter> T setScenarioTriggerIds(
      Set<String> scenarioTriggerIds) {
    this.scenarioTriggerIds = scenarioTriggerIds;
    return (T) this;
  }

  /** @return scenarioTrigger */
  @JsonIgnore
  public List<ScenarioTrigger> getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  /**
   * @param scenarioTrigger scenarioTrigger to set
   * @return ScenarioToTriggerFilter
   */
  public <T extends ScenarioToTriggerFilter> T setScenarioTrigger(
      List<ScenarioTrigger> scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }

  /** @return scenarioIds */
  public Set<String> getScenarioIds() {
    return this.scenarioIds;
  }

  /**
   * @param scenarioIds scenarioIds to set
   * @return ScenarioToTriggerFilter
   */
  public <T extends ScenarioToTriggerFilter> T setScenarioIds(Set<String> scenarioIds) {
    this.scenarioIds = scenarioIds;
    return (T) this;
  }

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return ScenarioToTriggerFilter
   */
  public <T extends ScenarioToTriggerFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
