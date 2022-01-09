package com.flexicore.rules.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.flexicore.file.model.FileResource;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class ScenarioTrigger extends SecuredBasic {

  private String lastEventId;

  private String lastActivated;

  private String validFrom;

  private String cooldownIntervalMs;

  private String activeTill;

  private String activeMs;

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource logFileResource;

  @ManyToOne(targetEntity = FileResource.class)
  private FileResource evaluatingJSCode;

  @ManyToOne(targetEntity = ScenarioTriggerType.class)
  private ScenarioTriggerType scenarioTriggerType;

  private String validTill;

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
  public String getLastActivated() {
    return this.lastActivated;
  }

  /**
   * @param lastActivated lastActivated to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setLastActivated(String lastActivated) {
    this.lastActivated = lastActivated;
    return (T) this;
  }

  /** @return validFrom */
  public String getValidFrom() {
    return this.validFrom;
  }

  /**
   * @param validFrom validFrom to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setValidFrom(String validFrom) {
    this.validFrom = validFrom;
    return (T) this;
  }

  /** @return cooldownIntervalMs */
  public String getCooldownIntervalMs() {
    return this.cooldownIntervalMs;
  }

  /**
   * @param cooldownIntervalMs cooldownIntervalMs to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setCooldownIntervalMs(String cooldownIntervalMs) {
    this.cooldownIntervalMs = cooldownIntervalMs;
    return (T) this;
  }

  /** @return activeTill */
  public String getActiveTill() {
    return this.activeTill;
  }

  /**
   * @param activeTill activeTill to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setActiveTill(String activeTill) {
    this.activeTill = activeTill;
    return (T) this;
  }

  /** @return activeMs */
  public String getActiveMs() {
    return this.activeMs;
  }

  /**
   * @param activeMs activeMs to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setActiveMs(String activeMs) {
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
  public String getValidTill() {
    return this.validTill;
  }

  /**
   * @param validTill validTill to set
   * @return ScenarioTrigger
   */
  public <T extends ScenarioTrigger> T setValidTill(String validTill) {
    this.validTill = validTill;
    return (T) this;
  }
}
