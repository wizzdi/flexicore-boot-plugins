package com.flexicore.rules.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DataSource extends SecuredBasic {

  @ManyToOne(targetEntity = DynamicExecution.class)
  private DynamicExecution dynamicExecution;
  @JsonIgnore
  @OneToMany(targetEntity = ScenarioToDataSource.class, mappedBy = "dataSource")
  private List<ScenarioToDataSource> scenarioToDataSources = new ArrayList<>();


  /** @return dynamicExecution */
  @ManyToOne(targetEntity = DynamicExecution.class)
  public DynamicExecution getDynamicExecution() {
    return this.dynamicExecution;
  }

  /**
   * @param dynamicExecution dynamicExecution to set
   * @return DataSource
   */
  public <T extends DataSource> T setDynamicExecution(DynamicExecution dynamicExecution) {
    this.dynamicExecution = dynamicExecution;
    return (T) this;
  }

  @JsonIgnore
  @OneToMany(targetEntity = ScenarioToDataSource.class, mappedBy = "dataSource")
  public List<ScenarioToDataSource> getScenarioToDataSources() {
    return scenarioToDataSources;
  }

  public <T extends DataSource> T setScenarioToDataSources(
          List<ScenarioToDataSource> scenarioToDataSources) {
    this.scenarioToDataSources = scenarioToDataSources;
    return (T) this;
  }
}
