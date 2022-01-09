package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.List;
import java.util.Set;

public class ScenarioFilter extends PaginationFilter {

  private Set<String> evaluatingJSCodeIds;

  private Set<String> logFileResourceIds;

  @JsonIgnore private List<FileResource> evaluatingJSCode;

  @JsonIgnore private List<FileResource> logFileResource;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> scenarioHint;

  public Set<String> getEvaluatingJSCodeIds() {
    return this.evaluatingJSCodeIds;
  }

  public <T extends ScenarioFilter> T setEvaluatingJSCodeIds(Set<String> evaluatingJSCodeIds) {
    this.evaluatingJSCodeIds = evaluatingJSCodeIds;
    return (T) this;
  }

  public Set<String> getLogFileResourceIds() {
    return this.logFileResourceIds;
  }

  public <T extends ScenarioFilter> T setLogFileResourceIds(Set<String> logFileResourceIds) {
    this.logFileResourceIds = logFileResourceIds;
    return (T) this;
  }

  @JsonIgnore
  public List<FileResource> getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  public <T extends ScenarioFilter> T setEvaluatingJSCode(List<FileResource> evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  @JsonIgnore
  public List<FileResource> getLogFileResource() {
    return this.logFileResource;
  }

  public <T extends ScenarioFilter> T setLogFileResource(List<FileResource> logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScenarioFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  public Set<String> getScenarioHint() {
    return this.scenarioHint;
  }

  public <T extends ScenarioFilter> T setScenarioHint(Set<String> scenarioHint) {
    this.scenarioHint = scenarioHint;
    return (T) this;
  }
}
