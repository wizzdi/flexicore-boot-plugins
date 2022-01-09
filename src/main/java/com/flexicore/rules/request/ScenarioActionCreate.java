package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScenarioActionCreate extends BasicCreate {

  @JsonIgnore private DynamicExecution dynamicExecution;

  private String dynamicExecutionId;

  @JsonIgnore
  public DynamicExecution getDynamicExecution() {
    return this.dynamicExecution;
  }

  public <T extends ScenarioActionCreate> T setDynamicExecution(DynamicExecution dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }

  public String getDynamicExecutionId() {
    return this.dynamicExecutionId;
  }

  public <T extends ScenarioActionCreate> T setDynamicExecutionId(String dynamicExecutionId) {
    this.dynamicExecutionId = dynamicExecutionId;
    return (T) this;
  }
}
