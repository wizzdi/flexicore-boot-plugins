package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicCreate;
import java.time.OffsetDateTime;

/** Object Used to Create ScenarioTrigger */
public class ScenarioTriggerCreate extends BasicCreate {

  private OffsetDateTime validFrom;

  private Long activeMs;

  @JsonIgnore private FileResource logFileResource;

  private Long cooldownIntervalMs;

  private String logFileResourceId;

  @JsonIgnore private ScenarioTriggerType scenarioTriggerType;

  private OffsetDateTime validTill;

  private OffsetDateTime activeTill;

  private String lastEventId;

  private String evaluatingJSCodeId;

  private String scenarioTriggerTypeId;

  private OffsetDateTime lastActivated;

  @JsonIgnore private FileResource evaluatingJSCode;

  /** @return validFrom */
  public OffsetDateTime getValidFrom() {
    return this.validFrom;
  }

  /**
   * @param validFrom validFrom to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setValidFrom(OffsetDateTime validFrom) {
    this.validFrom = validFrom;
    return (T) this;
  }

  /** @return activeMs */
  public Long getActiveMs() {
    return this.activeMs;
  }

  /**
   * @param activeMs activeMs to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setActiveMs(Long activeMs) {
    this.activeMs = activeMs;
    return (T) this;
  }

  /** @return logFileResource */
  @JsonIgnore
  public FileResource getLogFileResource() {
    return this.logFileResource;
  }

  /**
   * @param logFileResource logFileResource to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setLogFileResource(FileResource logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  /** @return cooldownIntervalMs */
  public Long getCooldownIntervalMs() {
    return this.cooldownIntervalMs;
  }

  /**
   * @param cooldownIntervalMs cooldownIntervalMs to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setCooldownIntervalMs(Long cooldownIntervalMs) {
    this.cooldownIntervalMs = cooldownIntervalMs;
    return (T) this;
  }

  /** @return logFileResourceId */
  public String getLogFileResourceId() {
    return this.logFileResourceId;
  }

  /**
   * @param logFileResourceId logFileResourceId to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setLogFileResourceId(String logFileResourceId) {
    this.logFileResourceId = logFileResourceId;
    return (T) this;
  }

  /** @return scenarioTriggerType */
  @JsonIgnore
  public ScenarioTriggerType getScenarioTriggerType() {
    return this.scenarioTriggerType;
  }

  /**
   * @param scenarioTriggerType scenarioTriggerType to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setScenarioTriggerType(
      ScenarioTriggerType scenarioTriggerType) {
    this.scenarioTriggerType = scenarioTriggerType;
    return (T) this;
  }

  /** @return validTill */
  public OffsetDateTime getValidTill() {
    return this.validTill;
  }

  /**
   * @param validTill validTill to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setValidTill(OffsetDateTime validTill) {
    this.validTill = validTill;
    return (T) this;
  }

  /** @return activeTill */
  public OffsetDateTime getActiveTill() {
    return this.activeTill;
  }

  /**
   * @param activeTill activeTill to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setActiveTill(OffsetDateTime activeTill) {
    this.activeTill = activeTill;
    return (T) this;
  }

  /** @return lastEventId */
  public String getLastEventId() {
    return this.lastEventId;
  }

  /**
   * @param lastEventId lastEventId to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setLastEventId(String lastEventId) {
    this.lastEventId = lastEventId;
    return (T) this;
  }

  /** @return evaluatingJSCodeId */
  public String getEvaluatingJSCodeId() {
    return this.evaluatingJSCodeId;
  }

  /**
   * @param evaluatingJSCodeId evaluatingJSCodeId to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setEvaluatingJSCodeId(String evaluatingJSCodeId) {
    this.evaluatingJSCodeId = evaluatingJSCodeId;
    return (T) this;
  }

  /** @return scenarioTriggerTypeId */
  public String getScenarioTriggerTypeId() {
    return this.scenarioTriggerTypeId;
  }

  /**
   * @param scenarioTriggerTypeId scenarioTriggerTypeId to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setScenarioTriggerTypeId(
      String scenarioTriggerTypeId) {
    this.scenarioTriggerTypeId = scenarioTriggerTypeId;
    return (T) this;
  }

  /** @return lastActivated */
  public OffsetDateTime getLastActivated() {
    return this.lastActivated;
  }

  /**
   * @param lastActivated lastActivated to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setLastActivated(OffsetDateTime lastActivated) {
    this.lastActivated = lastActivated;
    return (T) this;
  }

  /** @return evaluatingJSCode */
  @JsonIgnore
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return ScenarioTriggerCreate
   */
  public <T extends ScenarioTriggerCreate> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }
}
