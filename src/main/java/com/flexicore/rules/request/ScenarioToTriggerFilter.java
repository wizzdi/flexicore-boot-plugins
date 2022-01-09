package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.Scenario;
import com.flexicore.rules.model.ScenarioTrigger;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class ScenarioToTriggerFilter extends PaginationFilter {

  private Set<Boolean> firing;

  @JsonIgnore private List<ScenarioTrigger> scenarioTrigger;

  private Set<String> scenarioIds;

  private Set<String> scenarioTriggerIds;

  @JsonIgnore private List<Scenario> scenario;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<Integer> ordinal;

  private Set<Boolean> enabled;

  public Set<Boolean> getFiring() {
    return this.firing;
  }

  public <T extends ScenarioToTriggerFilter> T setFiring(Set<Boolean> firing) {
    this.firing = firing;
    return (T) this;
  }

  @JsonIgnore
  public List<ScenarioTrigger> getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  public <T extends ScenarioToTriggerFilter> T setScenarioTrigger(
      List<ScenarioTrigger> scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }

  public Set<String> getScenarioIds() {
    return this.scenarioIds;
  }

  public <T extends ScenarioToTriggerFilter> T setScenarioIds(Set<String> scenarioIds) {
    this.scenarioIds = scenarioIds;
    return (T) this;
  }

  public Set<String> getScenarioTriggerIds() {
    return this.scenarioTriggerIds;
  }

  public <T extends ScenarioToTriggerFilter> T setScenarioTriggerIds(
      Set<String> scenarioTriggerIds) {
    this.scenarioTriggerIds = scenarioTriggerIds;
    return (T) this;
  }

  @JsonIgnore
  public List<Scenario> getScenario() {
    return this.scenario;
  }

  public <T extends ScenarioToTriggerFilter> T setScenario(List<Scenario> scenario) {
    this.scenario = scenario;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScenarioToTriggerFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  public Set<Integer> getOrdinal() {
    return this.ordinal;
  }

  public <T extends ScenarioToTriggerFilter> T setOrdinal(Set<Integer> ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  public Set<Boolean> getEnabled() {
    return this.enabled;
  }

  public <T extends ScenarioToTriggerFilter> T setEnabled(Set<Boolean> enabled) {
    this.enabled = enabled;
    return (T) this;
  }
}
