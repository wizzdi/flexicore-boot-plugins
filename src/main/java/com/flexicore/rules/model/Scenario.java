package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.file.model.FileResource;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Scenario extends SecuredBasic {

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource evaluatingJSCode;

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource logFileResource;

  private String scenarioHint;

  /** @return evaluatingJSCode */
  @ManyToOne(targetEntity = FileResource.class)
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return Scenario
   */
  public <T extends Scenario> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  /** @return logFileResource */
  @ManyToOne(targetEntity = FileResource.class)
  public FileResource getLogFileResource() {
    return this.logFileResource;
  }

  /**
   * @param logFileResource logFileResource to set
   * @return Scenario
   */
  public <T extends Scenario> T setLogFileResource(FileResource logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  /** @return scenarioHint */
  public String getScenarioHint() {
    return this.scenarioHint;
  }

  /**
   * @param scenarioHint scenarioHint to set
   * @return Scenario
   */
  public <T extends Scenario> T setScenarioHint(String scenarioHint) {
    this.scenarioHint = scenarioHint;
    return (T) this;
  }
}
