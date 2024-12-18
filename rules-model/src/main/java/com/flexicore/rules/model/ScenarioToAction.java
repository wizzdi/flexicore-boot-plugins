package com.flexicore.rules.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ScenarioToAction extends Baseclass {

  private boolean enabled;

  @ManyToOne(targetEntity = ScenarioAction.class)
  private ScenarioAction scenarioAction;

  @ManyToOne(targetEntity = Scenario.class)
  private Scenario scenario;

  /** @return enabled */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToAction
   */
  public <T extends ScenarioToAction> T setEnabled(boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  /** @return scenarioAction */
  @ManyToOne(targetEntity = ScenarioAction.class)
  public ScenarioAction getScenarioAction() {
    return this.scenarioAction;
  }

  /**
   * @param scenarioAction scenarioAction to set
   * @return ScenarioToAction
   */
  public <T extends ScenarioToAction> T setScenarioAction(ScenarioAction scenarioAction) {
    this.scenarioAction = scenarioAction;
    return (T) this;
  }

  /** @return scenario */
  @ManyToOne(targetEntity = Scenario.class)
  public Scenario getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioToAction
   */
  public <T extends ScenarioToAction> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }
}
