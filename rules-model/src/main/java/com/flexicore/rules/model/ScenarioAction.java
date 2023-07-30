package com.flexicore.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ScenarioAction extends SecuredBasic {

  @ManyToOne(targetEntity = DynamicExecution.class)
  private DynamicExecution dynamicExecution;
  @JsonIgnore
  @OneToMany(targetEntity = ScenarioToAction.class, mappedBy = "scenarioAction")
  private List<ScenarioToAction> scenarioToActions = new ArrayList<>();

  @Schema(name = "Scenarios", description = "A list of ScenarioToAction instances of all Scenarios connected to this ScenarioAction")
  @JsonIgnore
  @OneToMany(targetEntity = ScenarioToAction.class, mappedBy = "scenarioAction")
  public List<ScenarioToAction> getScenarioToActions() {
    return scenarioToActions;
  }

  public <T extends ScenarioAction> T setScenarioToActions(
          List<ScenarioToAction> scenarioToActions) {
    this.scenarioToActions = scenarioToActions;
    return (T) this;
  }

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
