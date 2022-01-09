package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class ScenarioActionFilter extends PaginationFilter {

  @JsonIgnore private List<DynamicExecution> dynamicExecution;

  private Set<String> dynamicExecutionIds;

  private BasicPropertiesFilter basicPropertiesFilter;

  @JsonIgnore
  public List<DynamicExecution> getDynamicExecution() {
    return this.dynamicExecution;
  }

  public <T extends ScenarioActionFilter> T setDynamicExecution(
      List<DynamicExecution> dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }

  public Set<String> getDynamicExecutionIds() {
    return this.dynamicExecutionIds;
  }

  public <T extends ScenarioActionFilter> T setDynamicExecutionIds(
      Set<String> dynamicExecutionIds) {
    this.dynamicExecutionIds = dynamicExecutionIds;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScenarioActionFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
