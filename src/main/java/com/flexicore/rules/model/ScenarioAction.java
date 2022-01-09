package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ScenarioAction extends SecuredBasic {

  @ManyToOne(targetEntity = DynamicExecution.class)
  private DynamicExecution dynamicExecution;

  /** @return dynamicExecution */
  @ManyToOne(targetEntity = DynamicExecution.class)
  public DynamicExecution getDynamicExecution() {
    return this.dynamicExecution;
  }

  /**
   * @param dynamicExecution dynamicExecution to set
   * @return ScenarioAction
   */
  public <T extends ScenarioAction> T setDynamicExecution(DynamicExecution dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }
}
