package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScenarioCreate extends BasicCreate {

  private String evaluatingJSCodeId;

  private String logFileResourceId;

  @JsonIgnore private FileResource evaluatingJSCode;

  @JsonIgnore private FileResource logFileResource;

  private String scenarioHint;

  public String getEvaluatingJSCodeId() {
    return this.evaluatingJSCodeId;
  }

  public <T extends ScenarioCreate> T setEvaluatingJSCodeId(String evaluatingJSCodeId) {
    this.evaluatingJSCodeId = evaluatingJSCodeId;
    return (T) this;
  }

  public String getLogFileResourceId() {
    return this.logFileResourceId;
  }

  public <T extends ScenarioCreate> T setLogFileResourceId(String logFileResourceId) {
    this.logFileResourceId = logFileResourceId;
    return (T) this;
  }

  @JsonIgnore
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  public <T extends ScenarioCreate> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  @JsonIgnore
  public FileResource getLogFileResource() {
    return this.logFileResource;
  }

  public <T extends ScenarioCreate> T setLogFileResource(FileResource logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  public String getScenarioHint() {
    return this.scenarioHint;
  }

  public <T extends ScenarioCreate> T setScenarioHint(String scenarioHint) {
    this.scenarioHint = scenarioHint;
    return (T) this;
  }
}
