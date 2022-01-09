package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.file.model.FileResource;
import java.time.OffsetDateTime;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ScenarioTrigger extends SecuredBasic {

  private String lastEventId;

  private OffsetDateTime lastActivated;

  private OffsetDateTime validFrom;

  private Long cooldownIntervalMs;

  private OffsetDateTime activeTill;

  private Long activeMs;

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource logFileResource;

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource evaluatingJSCode;

  @ManyToOne(targetEntity = ScenarioTriggerType.class)
  private ScenarioTriggerType scenarioTriggerType;

  private OffsetDateTime validTill;

  /** @return lastEventId */
  public String getLastEventId() {
    return this.lastEventId;
  }

  /**
   * @param lastEventId lastEventId to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setLastEventId(String lastEventId) {
    this.lastEventId = lastEventId;
    return (T) this;
  }

  /** @return lastActivated */
  public OffsetDateTime getLastActivated() {
    return this.lastActivated;
  }

  /**
   * @param lastActivated lastActivated to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setLastActivated(OffsetDateTime lastActivated) {
    this.lastActivated = lastActivated;
    return (T) this;
  }

  /** @return validFrom */
  public OffsetDateTime getValidFrom() {
    return this.validFrom;
  }

  /**
   * @param validFrom validFrom to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setValidFrom(OffsetDateTime validFrom) {
    this.validFrom = validFrom;
    return (T) this;
  }

  /** @return cooldownIntervalMs */
  public Long getCooldownIntervalMs() {
    return this.cooldownIntervalMs;
  }

  /**
   * @param cooldownIntervalMs cooldownIntervalMs to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setCooldownIntervalMs(Long cooldownIntervalMs) {
    this.cooldownIntervalMs = cooldownIntervalMs;
    return (T) this;
  }

  /** @return activeTill */
  public OffsetDateTime getActiveTill() {
    return this.activeTill;
  }

  /**
   * @param activeTill activeTill to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setActiveTill(OffsetDateTime activeTill) {
    this.activeTill = activeTill;
    return (T) this;
  }

  /** @return activeMs */
  public Long getActiveMs() {
    return this.activeMs;
  }

  /**
   * @param activeMs activeMs to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setActiveMs(Long activeMs) {
    this.activeMs = activeMs;
    return (T) this;
  }

  /** @return logFileResource */
  @ManyToOne(targetEntity = FileResource.class)
  public FileResource getLogFileResource() {
    return this.logFileResource;
  }

  /**
   * @param logFileResource logFileResource to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setLogFileResource(FileResource logFileResource) {
    this.logFileResource = logFileResource;
    return (T) this;
  }

  /** @return evaluatingJSCode */
  @ManyToOne(targetEntity = FileResource.class)
  public FileResource getEvaluatingJSCode() {
    return this.evaluatingJSCode;
  }

  /**
   * @param evaluatingJSCode evaluatingJSCode to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setEvaluatingJSCode(FileResource evaluatingJSCode) {
    this.evaluatingJSCode = evaluatingJSCode;
    return (T) this;
  }

  /** @return scenarioTriggerType */
  @ManyToOne(targetEntity = ScenarioTriggerType.class)
  public ScenarioTriggerType getScenarioTriggerType() {
    return this.scenarioTriggerType;
  }

  /**
   * @param scenarioTriggerType scenarioTriggerType to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setScenarioTriggerType(
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
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setValidTill(OffsetDateTime validTill) {
    this.validTill = validTill;
    return (T) this;
  }
}
