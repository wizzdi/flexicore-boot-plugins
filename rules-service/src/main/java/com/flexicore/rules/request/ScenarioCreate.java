package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create Scenario */
public class ScenarioCreate extends BasicCreate {

  private String logFileResourceId;

  @JsonIgnore private FileResource logFileResource;

  private String evaluatingJSCodeId;

  private String scenarioHint;

  @JsonIgnore private FileResource evaluatingJSCode;

  /** @return logFileResourceId */
  public String getLogFileResourceId() {
    return this.logFileResourceId;
  }

  /**
   * @param logFileResourceId logFileResourceId to set
   * @return ScenarioCreate
   */
  public <T extends ScenarioCreate> T setLogFileResourceId(String logFileResourceId) {
    this.logFileResourceId = logFileResourceId;
    return (T) this;
  }

  /** @return logFileResource */
  @JsonIgnore
  public FileResource getLogFileResource() {
    return this.logFileResource;
  }

  /**
   * @param logFileResource logFileResource to set
   * @return ScenarioCreate
   */
  public <T extends ScenarioCreate> T setLogFileResource(FileResource logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  /** @return evaluatingJSCodeId */
  public String getEvaluatingJSCodeId() {
    return this.evaluatingJSCodeId;
  }

  /**
   * @param evaluatingJSCodeId evaluatingJSCodeId to set
   * @return ScenarioCreate
   */
  public <T extends ScenarioCreate> T setEvaluatingJSCodeId(String evaluatingJSCodeId) {
    this.evaluatingJSCodeId = evaluatingJSCodeId;
    return (T) this;
  }

  /** @return scenarioHint */
  public String getScenarioHint() {
    return this.scenarioHint;
  }

  /**
   * @param scenarioHint scenarioHint to set
   * @return ScenarioCreate
   */
  public <T extends ScenarioCreate> T setScenarioHint(String scenarioHint) {
    this.scenarioHint = scenarioHint;
    return (T) this;
  }

  /** @return evaluatingJSCode */
  @JsonIgnore
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return ScenarioCreate
   */
  public <T extends ScenarioCreate> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }
}
