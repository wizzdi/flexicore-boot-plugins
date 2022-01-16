package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create DataSource */
public class DataSourceCreate extends BasicCreate {

  private String dynamicExecutionId;

  @JsonIgnore private DynamicExecution dynamicExecution;

  /** @return dynamicExecutionId */
  public String getDynamicExecutionId() {
    return this.dynamicExecutionId;
  }

  /**
   * @param dynamicExecutionId dynamicExecutionId to set
   * @return DataSourceCreate
   */
  public <T extends DataSourceCreate> T setDynamicExecutionId(String dynamicExecutionId) {
    this.dynamicExecutionId = dynamicExecutionId;
    return (T) this;
  }

  /** @return dynamicExecution */
  @JsonIgnore
  public DynamicExecution getDynamicExecution() {
    return this.dynamicExecution;
  }

  /**
   * @param dynamicExecution dynamicExecution to set
   * @return DataSourceCreate
   */
  public <T extends DataSourceCreate> T setDynamicExecution(DynamicExecution dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }
}
