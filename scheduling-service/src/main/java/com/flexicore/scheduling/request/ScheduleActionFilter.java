package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class ScheduleActionFilter extends PaginationFilter {

  private Set<String> dynamicExecutionIds;

  @JsonIgnore private List<DynamicExecution> dynamicExecution;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<String> getDynamicExecutionIds() {
    return this.dynamicExecutionIds;
  }

  public <T extends ScheduleActionFilter> T setDynamicExecutionIds(
      Set<String> dynamicExecutionIds) {
    this.dynamicExecutionIds = dynamicExecutionIds;
    return (T) this;
  }

  @JsonIgnore
  public List<DynamicExecution> getDynamicExecution() {
    return this.dynamicExecution;
  }

  public <T extends ScheduleActionFilter> T setDynamicExecution(
      List<DynamicExecution> dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScheduleActionFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
