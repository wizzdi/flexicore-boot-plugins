package com.flexicore.scheduling.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScheduleActionCreate extends BasicCreate {

  private String dynamicExecutionId;

  @JsonIgnore private DynamicExecution dynamicExecution;

  public String getDynamicExecutionId() {
    return this.dynamicExecutionId;
  }

  public <T extends ScheduleActionCreate> T setDynamicExecutionId(String dynamicExecutionId) {
    this.dynamicExecutionId = dynamicExecutionId;
    return (T) this;
  }

  @JsonIgnore
  public DynamicExecution getDynamicExecution() {
    return this.dynamicExecution;
  }

  public <T extends ScheduleActionCreate> T setDynamicExecution(DynamicExecution dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }
}
