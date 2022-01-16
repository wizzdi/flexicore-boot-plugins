package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

/** Object Used to List Scenario */
public class ScenarioFilter extends PaginationFilter {

  @JsonIgnore private List<FileResource> evaluatingJSCode;

  private Set<String> logFileResourceIds;

  @JsonIgnore private List<FileResource> logFileResource;

  private Set<String> evaluatingJSCodeIds;

  private Set<String> scenarioHint;

  /** @return evaluatingJSCode */
  @JsonIgnore
  public List<FileResource> getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return ScenarioFilter
   */
  public <T extends ScenarioFilter> T setEvaluatingJSCode(List<FileResource> evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  /** @return logFileResourceIds */
  public Set<String> getLogFileResourceIds() {
    return this.logFileResourceIds;
  }

  /**
   * @param logFileResourceIds logFileResourceIds to set
   * @return ScenarioFilter
   */
  public <T extends ScenarioFilter> T setLogFileResourceIds(Set<String> logFileResourceIds) {
    this.logFileResourceIds = logFileResourceIds;
    return (T) this;
  }

  /** @return logFileResource */
  @JsonIgnore
  public List<FileResource> getLogFileResource() {
    return this.logFileResource;
  }

  /**
   * @param logFileResource logFileResource to set
   * @return ScenarioFilter
   */
  public <T extends ScenarioFilter> T setLogFileResource(List<FileResource> logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  /** @return evaluatingJSCodeIds */
  public Set<String> getEvaluatingJSCodeIds() {
    return this.evaluatingJSCodeIds;
  }

  /**
   * @param evaluatingJSCodeIds evaluatingJSCodeIds to set
   * @return ScenarioFilter
   */
  public <T extends ScenarioFilter> T setEvaluatingJSCodeIds(Set<String> evaluatingJSCodeIds) {
    this.evaluatingJSCodeIds = evaluatingJSCodeIds;
    return (T) this;
  }

  /** @return scenarioHint */
  public Set<String> getScenarioHint() {
    return this.scenarioHint;
  }

  /**
   * @param scenarioHint scenarioHint to set
   * @return ScenarioFilter
   */
  public <T extends ScenarioFilter> T setScenarioHint(Set<String> scenarioHint) {
    this.scenarioHint = scenarioHint;
    return (T) this;
  }
}
