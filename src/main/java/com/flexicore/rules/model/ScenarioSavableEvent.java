package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ScenarioSavableEvent extends SecuredBasic {

  @ManyToOne(targetEntity = ScenarioTrigger.class)
  private ScenarioTrigger scenarioTrigger;

  /** @return scenarioTrigger */
  @ManyToOne(targetEntity = ScenarioTrigger.class)
  public ScenarioTrigger getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  /**
   * @param scenarioTrigger scenarioTrigger to set
   * @return ScenarioSavableEvent
   */
  public <T extends ScenarioSavableEvent> T setScenarioTrigger(ScenarioTrigger scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }
}
