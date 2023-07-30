package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ScenarioToDataSource extends SecuredBasic {

  private boolean enabled;

  @ManyToOne(targetEntity = DataSource.class)
  private DataSource dataSource;

  private Integer ordinal;

  @ManyToOne(targetEntity = Scenario.class)
  private Scenario scenario;

  /** @return enabled */
  public boolean isEnabled() {
    return this.enabled;
  }

  /**
   * @param enabled enabled to set
   * @return ScenarioToDataSource
   */
  public <T extends ScenarioToDataSource> T setEnabled(boolean enabled) {
    this.enabled = enabled;
    return (T) this;
  }

  /** @return dataSource */
  @ManyToOne(targetEntity = DataSource.class)
  public DataSource getDataSource() {
    return this.dataSource;
  }

  /**
   * @param dataSource dataSource to set
   * @return ScenarioToDataSource
   */
  public <T extends ScenarioToDataSource> T setDataSource(DataSource dataSource) {
    this.dataSource = dataSource;
    return (T) this;
  }

  /** @return ordinal */
  public Integer getOrdinal() {
    return this.ordinal;
  }

  /**
   * @param ordinal ordinal to set
   * @return ScenarioToDataSource
   */
  public <T extends ScenarioToDataSource> T setOrdinal(Integer ordinal) {
    this.ordinal = ordinal;
    return (T) this;
  }

  /** @return scenario */
  @ManyToOne(targetEntity = Scenario.class)
  public Scenario getScenario() {
    return this.scenario;
  }

  /**
   * @param scenario scenario to set
   * @return ScenarioToDataSource
   */
  public <T extends ScenarioToDataSource> T setScenario(Scenario scenario) {
    this.scenario = scenario;
    return (T) this;
  }
}
