package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

/** Object Used to List ScenarioAction */
public class ScenarioActionFilter extends PaginationFilter {

  private Set<String> dynamicExecutionIds;

  @JsonIgnore private List<DynamicExecution> dynamicExecution;

  private BasicPropertiesFilter basicPropertiesFilter;

  /** @return dynamicExecutionIds */
  public Set<String> getDynamicExecutionIds() {
    return this.dynamicExecutionIds;
  }

  /**
   * @param dynamicExecutionIds dynamicExecutionIds to set
   * @return ScenarioActionFilter
   */
  public <T extends ScenarioActionFilter> T setDynamicExecutionIds(
      Set<String> dynamicExecutionIds) {
    this.dynamicExecutionIds = dynamicExecutionIds;
    return (T) this;
  }

  /** @return dynamicExecution */
  @JsonIgnore
  public List<DynamicExecution> getDynamicExecution() {
    return this.dynamicExecution;
  }

  /**
   * @param dynamicExecution dynamicExecution to set
   * @return ScenarioActionFilter
   */
  public <T extends ScenarioActionFilter> T setDynamicExecution(
      List<DynamicExecution> dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return ScenarioActionFilter
   */
  public <T extends ScenarioActionFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
