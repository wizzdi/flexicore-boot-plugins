package com.flexicore.rules.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ScenarioToTrigger extends Baseclass {

  private boolean firing;

  @ManyToOne(targetEntity = ScenarioTrigger.class)
  private ScenarioTrigger scenarioTrigger;

  @ManyToOne(targetEntity = Scenario.class)
  private Scenario scenario;


  private boolean enabled;

  /** @return firing */
  public boolean isFiring() {
    return this.firing;
  }

  /**
   * @param firing firing to set
   * @return ScenarioToTrigger
   */
  public <T extends ScenarioToTrigger> T setFiring(boolean firing) {
    this.firing = firing;
    return (T) this;
  }

  /** @return scenarioTrigger */
  @ManyToOne(targetEntity = ScenarioTrigger.class)
  public ScenarioTrigger getScenarioTrigger() {
    return this.scenarioTrigger;
  }

  /**
   * @param scenarioTrigger scenarioTrigger to set
   * @return ScenarioToTrigger
   */
  public <T extends ScenarioToTrigger> T setScenarioTrigger(ScenarioTrigger scenarioTrigger) {
    this.scenarioTrigger = scenarioTrigger;
    return (T) this;
  }

  /** @return scenario */
  @ManyToOne(targetEntity = Scenario.class)
  public Scenario getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioToTrigger
   */
  public <T extends ScenarioToTrigger> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }


  /** @return enabled */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToTrigger
   */
  public <T extends ScenarioToTrigger> T setEnabled(boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }
}
