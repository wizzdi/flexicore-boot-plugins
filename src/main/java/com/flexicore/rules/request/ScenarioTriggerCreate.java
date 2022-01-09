package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;

public class ScenarioTriggerCreate extends BasicCreate {

  private String lastEventId;

  private String lastActivated;

  private String validFrom;

  private String cooldownIntervalMs;

  private String logFileResourceId;

  @JsonIgnore private ScenarioTriggerType scenarioTriggerType;

  private String validTill;

  private String scenarioTriggerTypeId;

  private String evaluatingJSCodeId;

  private String activeTill;

  private String activeMs;

  @JsonIgnore private FileResource logFileResource;

  @JsonIgnore private FileResource evaluatingJSCode;

  public String getLastEventId() {
    return this.lastEventId;
  }

  public <T extends ScenarioTriggerCreate> T setLastEventId(String lastEventId) {
    this.lastEventId = lastEventId;
    return (T) this;
  }

  public String getLastActivated() {
    return this.lastActivated;
  }

  public <T extends ScenarioTriggerCreate> T setLastActivated(String lastActivated) {
    this.lastActivated = lastActivated;
    return (T) this;
  }

  public String getValidFrom() {
    return this.validFrom;
  }

  public <T extends ScenarioTriggerCreate> T setValidFrom(String validFrom) {
    this.validFrom = validFrom;
    return (T) this;
  }

  public String getCooldownIntervalMs() {
    return this.cooldownIntervalMs;
  }

  public <T extends ScenarioTriggerCreate> T setCooldownIntervalMs(String cooldownIntervalMs) {
    this.cooldownIntervalMs = cooldownIntervalMs;
    return (T) this;
  }

  public String getLogFileResourceId() {
    return this.logFileResourceId;
  }

  public <T extends ScenarioTriggerCreate> T setLogFileResourceId(String logFileResourceId) {
    this.logFileResourceId = logFileResourceId;
    return (T) this;
  }

  @JsonIgnore
  public ScenarioTriggerType getScenarioTriggerType() {
    return this.scenarioTriggerType;
  }

  public <T extends ScenarioTriggerCreate> T setScenarioTriggerType(
      ScenarioTriggerType scenarioTriggerType) {
    this.scenarioTriggerType = scenarioTriggerType;
    return (T) this;
  }

  public String getValidTill() {
    return this.validTill;
  }

  public <T extends ScenarioTriggerCreate> T setValidTill(String validTill) {
    this.validTill = validTill;
    return (T) this;
  }

  public String getScenarioTriggerTypeId() {
    return this.scenarioTriggerTypeId;
  }

  public <T extends ScenarioTriggerCreate> T setScenarioTriggerTypeId(
      String scenarioTriggerTypeId) {
    this.scenarioTriggerTypeId = scenarioTriggerTypeId;
    return (T) this;
  }

  public String getEvaluatingJSCodeId() {
    return this.evaluatingJSCodeId;
  }

  public <T extends ScenarioTriggerCreate> T setEvaluatingJSCodeId(String evaluatingJSCodeId) {
    this.evaluatingJSCodeId = evaluatingJSCodeId;
    return (T) this;
  }

  public String getActiveTill() {
    return this.activeTill;
  }

  public <T extends ScenarioTriggerCreate> T setActiveTill(String activeTill) {
    this.activeTill = activeTill;
    return (T) this;
  }

  public String getActiveMs() {
    return this.activeMs;
  }

  public <T extends ScenarioTriggerCreate> T setActiveMs(String activeMs) {
    this.activeMs = activeMs;
    return (T) this;
  }

  @JsonIgnore
  public FileResource getLogFileResource() {
    return this.logFileResource;
  }

  public <T extends ScenarioTriggerCreate> T setLogFileResource(FileResource logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  @JsonIgnore
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  public <T extends ScenarioTriggerCreate> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }
}
