package com.flexicore.rules.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.rules.model.ScenarioTriggerType;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

public class ScenarioTriggerFilter extends PaginationFilter {

  private Set<String> lastEventId;

  private Set<OffsetDateTime> lastActivated;

  private Set<OffsetDateTime> validFrom;

  private Set<Long> cooldownIntervalMs;

  private Set<String> logFileResourceIds;

  @JsonIgnore private List<ScenarioTriggerType> scenarioTriggerType;

  private Set<OffsetDateTime> validTill;

  private Set<String> scenarioTriggerTypeIds;

  private Set<String> evaluatingJSCodeIds;

  private Set<OffsetDateTime> activeTill;

  private Set<Long> activeMs;

  @JsonIgnore private List<FileResource> logFileResource;

  @JsonIgnore private List<FileResource> evaluatingJSCode;


  private BasicPropertiesFilter basicPropertiesFilter;
  private Set<String> eventCanonicalNames;

  public Set<String> getLastEventId() {
    return this.lastEventId;
  }

  public <T extends ScenarioTriggerFilter> T setLastEventId(Set<String> lastEventId) {
    this.lastEventId = lastEventId;
    return (T) this;
  }

  public Set<OffsetDateTime> getLastActivated() {
    return this.lastActivated;
  }

  public <T extends ScenarioTriggerFilter> T setLastActivated(Set<OffsetDateTime> lastActivated) {
    this.lastActivated = lastActivated;
    return (T) this;
  }

  public Set<OffsetDateTime> getValidFrom() {
    return this.validFrom;
  }

  public <T extends ScenarioTriggerFilter> T setValidFrom(Set<OffsetDateTime> validFrom) {
    this.validFrom = validFrom;
    return (T) this;
  }

  public Set<Long> getCooldownIntervalMs() {
    return this.cooldownIntervalMs;
  }

  public <T extends ScenarioTriggerFilter> T setCooldownIntervalMs(Set<Long> cooldownIntervalMs) {
    this.cooldownIntervalMs = cooldownIntervalMs;
    return (T) this;
  }

  public Set<String> getLogFileResourceIds() {
    return this.logFileResourceIds;
  }

  public <T extends ScenarioTriggerFilter> T setLogFileResourceIds(Set<String> logFileResourceIds) {
    this.logFileResourceIds = logFileResourceIds;
    return (T) this;
  }

  @JsonIgnore
  public List<ScenarioTriggerType> getScenarioTriggerType() {
    return this.scenarioTriggerType;
  }

  public <T extends ScenarioTriggerFilter> T setScenarioTriggerType(
      List<ScenarioTriggerType> scenarioTriggerType) {
    this.scenarioTriggerType = scenarioTriggerType;
    return (T) this;
  }

  public Set<OffsetDateTime> getValidTill() {
    return this.validTill;
  }

  public <T extends ScenarioTriggerFilter> T setValidTill(Set<OffsetDateTime> validTill) {
    this.validTill = validTill;
    return (T) this;
  }

  public Set<String> getScenarioTriggerTypeIds() {
    return this.scenarioTriggerTypeIds;
  }

  public <T extends ScenarioTriggerFilter> T setScenarioTriggerTypeIds(
      Set<String> scenarioTriggerTypeIds) {
    this.scenarioTriggerTypeIds = scenarioTriggerTypeIds;
    return (T) this;
  }

  public Set<String> getEvaluatingJSCodeIds() {
    return this.evaluatingJSCodeIds;
  }

  public <T extends ScenarioTriggerFilter> T setEvaluatingJSCodeIds(
      Set<String> evaluatingJSCodeIds) {
    this.evaluatingJSCodeIds = evaluatingJSCodeIds;
    return (T) this;
  }

  public Set<OffsetDateTime> getActiveTill() {
    return this.activeTill;
  }

  public <T extends ScenarioTriggerFilter> T setActiveTill(Set<OffsetDateTime> activeTill) {
    this.activeTill = activeTill;
    return (T) this;
  }

  public Set<Long> getActiveMs() {
    return this.activeMs;
  }

  public <T extends ScenarioTriggerFilter> T setActiveMs(Set<Long> activeMs) {
    this.activeMs = activeMs;
    return (T) this;
  }

  @JsonIgnore
  public List<FileResource> getLogFileResource() {
    return this.logFileResource;
  }

  public <T extends ScenarioTriggerFilter> T setLogFileResource(
      List<FileResource> logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  @JsonIgnore
  public List<FileResource> getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  public <T extends ScenarioTriggerFilter> T setEvaluatingJSCode(
      List<FileResource> evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends ScenarioTriggerFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  public <T extends ScenarioTriggerFilter> T setEventCanonicalNames(Set<String> eventCanonicalNames) {
    this.eventCanonicalNames = eventCanonicalNames;
    return (T) this;

  }

  public Set<String> getEventCanonicalNames() {
    return eventCanonicalNames;
  }
}
