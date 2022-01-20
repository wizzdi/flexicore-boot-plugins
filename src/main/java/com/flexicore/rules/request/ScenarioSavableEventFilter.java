package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTrigger;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

/** Object Used to List ScenarioSavableEvent */
public class ScenarioSavableEventFilter extends PaginationFilter {

  @JsonIgnore private List<ScenarioTrigger> scenarioTrigger;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> scenarioTriggerIds;

  /** @return scenarioTrigger */
  @JsonIgnore
  public List<ScenarioTrigger> getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  /**
   * @param scenarioTrigger scenarioTrigger to set
   * @return ScenarioSavableEventFilter
   */
  public <T extends ScenarioSavableEventFilter> T setScenarioTrigger(
      List<ScenarioTrigger> scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return ScenarioSavableEventFilter
   */
  public <T extends ScenarioSavableEventFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return scenarioTriggerIds */
  public Set<String> getScenarioTriggerIds() {
    return this.scenarioTriggerIds;
  }

  /**
   * @param scenarioTriggerIds scenarioTriggerIds to set
   * @return ScenarioSavableEventFilter
   */
  public <T extends ScenarioSavableEventFilter> T setScenarioTriggerIds(
      Set<String> scenarioTriggerIds) {
    this.scenarioTriggerIds = scenarioTriggerIds;
    return (T) this;
  }
}
