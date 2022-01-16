package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/** Object Used to List ScenarioTrigger */
public class ScenarioTriggerFilter extends PaginationFilter {

  @JsonIgnore private List<FileResource> logFileResource;

  private Set<OffsetDateTime> validFrom;

  private Set<String> lastEventId;

  private Set<Long> cooldownIntervalMs;

  private Set<String> scenarioTriggerTypeIds;

  private Set<String> evaluatingJSCodeIds;

  private Set<OffsetDateTime> validTill;

  private Set<OffsetDateTime> activeTill;

  private Set<String> logFileResourceIds;

  @JsonIgnore private List<FileResource> evaluatingJSCode;

  private Set<OffsetDateTime> lastActivated;

  @JsonIgnore private List<ScenarioTriggerType> scenarioTriggerType;

  private Set<Long> activeMs;

  /** @return logFileResource */
  @JsonIgnore
  public List<FileResource> getLogFileResource() {
    return this.logFileResource;
  }

  /**
   * @param logFileResource logFileResource to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setLogFileResource(
      List<FileResource> logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  /** @return validFrom */
  public Set<OffsetDateTime> getValidFrom() {
    return this.validFrom;
  }

  /**
   * @param validFrom validFrom to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setValidFrom(Set<OffsetDateTime> validFrom) {
    this.validFrom = validFrom;
    return (T) this;
  }

  /** @return lastEventId */
  public Set<String> getLastEventId() {
    return this.lastEventId;
  }

  /**
   * @param lastEventId lastEventId to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setLastEventId(Set<String> lastEventId) {
    this.lastEventId = lastEventId;
    return (T) this;
  }

  /** @return cooldownIntervalMs */
  public Set<Long> getCooldownIntervalMs() {
    return this.cooldownIntervalMs;
  }

  /**
   * @param cooldownIntervalMs cooldownIntervalMs to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setCooldownIntervalMs(Set<Long> cooldownIntervalMs) {
    this.cooldownIntervalMs = cooldownIntervalMs;
    return (T) this;
  }

  /** @return scenarioTriggerTypeIds */
  public Set<String> getScenarioTriggerTypeIds() {
    return this.scenarioTriggerTypeIds;
  }

  /**
   * @param scenarioTriggerTypeIds scenarioTriggerTypeIds to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setScenarioTriggerTypeIds(
      Set<String> scenarioTriggerTypeIds) {
    this.scenarioTriggerTypeIds = scenarioTriggerTypeIds;
    return (T) this;
  }

  /** @return evaluatingJSCodeIds */
  public Set<String> getEvaluatingJSCodeIds() {
    return this.evaluatingJSCodeIds;
  }

  /**
   * @param evaluatingJSCodeIds evaluatingJSCodeIds to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setEvaluatingJSCodeIds(
      Set<String> evaluatingJSCodeIds) {
    this.evaluatingJSCodeIds = evaluatingJSCodeIds;
    return (T) this;
  }

  /** @return validTill */
  public Set<OffsetDateTime> getValidTill() {
    return this.validTill;
  }

  /**
   * @param validTill validTill to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setValidTill(Set<OffsetDateTime> validTill) {
    this.validTill = validTill;
    return (T) this;
  }

  /** @return activeTill */
  public Set<OffsetDateTime> getActiveTill() {
    return this.activeTill;
  }

  /**
   * @param activeTill activeTill to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setActiveTill(Set<OffsetDateTime> activeTill) {
    this.activeTill = activeTill;
    return (T) this;
  }

  /** @return logFileResourceIds */
  public Set<String> getLogFileResourceIds() {
    return this.logFileResourceIds;
  }

  /**
   * @param logFileResourceIds logFileResourceIds to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setLogFileResourceIds(Set<String> logFileResourceIds) {
    this.logFileResourceIds = logFileResourceIds;
    return (T) this;
  }

  /** @return evaluatingJSCode */
  @JsonIgnore
  public List<FileResource> getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setEvaluatingJSCode(
      List<FileResource> evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  /** @return lastActivated */
  public Set<OffsetDateTime> getLastActivated() {
    return this.lastActivated;
  }

  /**
   * @param lastActivated lastActivated to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setLastActivated(Set<OffsetDateTime> lastActivated) {
    this.lastActivated = lastActivated;
    return (T) this;
  }

  /** @return scenarioTriggerType */
  @JsonIgnore
  public List<ScenarioTriggerType> getScenarioTriggerType() {
    return this.scenarioTriggerType;
  }

  /**
   * @param scenarioTriggerType scenarioTriggerType to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setScenarioTriggerType(
      List<ScenarioTriggerType> scenarioTriggerType) {
    this.scenarioTriggerType = scenarioTriggerType;
    return (T) this;
  }

  /** @return activeMs */
  public Set<Long> getActiveMs() {
    return this.activeMs;
  }

  /**
   * @param activeMs activeMs to set
   * @return ScenarioTriggerFilter
   */
  public <T extends ScenarioTriggerFilter> T setActiveMs(Set<Long> activeMs) {
    this.activeMs = activeMs;
    return (T) this;
  }
}
