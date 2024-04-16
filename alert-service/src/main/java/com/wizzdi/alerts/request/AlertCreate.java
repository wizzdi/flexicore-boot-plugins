package com.wizzdi.alerts.request;

import com.wizzdi.alerts.AlertLevel;
import com.wizzdi.flexicore.security.request.BasicCreate;

import java.time.OffsetDateTime;

/** Object Used to Create Alert */
public class AlertCreate extends BasicCreate {

  private String alertCategory;

  private String alertContent;

  private AlertLevel alertLevel;

  private String relatedId;

  private String relatedType;

  private OffsetDateTime handledAt;

  /**
   * @return alertCategory
   */
  public String getAlertCategory() {
    return this.alertCategory;
  }

  /**
   * @param alertCategory alertCategory to set
   * @return AlertCreate
   */
  public <T extends AlertCreate> T setAlertCategory(String alertCategory) {
    this.alertCategory = alertCategory;
    return (T) this;
  }

  /**
   * @return alertContent
   */
  public String getAlertContent() {
    return this.alertContent;
  }

  /**
   * @param alertContent alertContent to set
   * @return AlertCreate
   */
  public <T extends AlertCreate> T setAlertContent(String alertContent) {
    this.alertContent = alertContent;
    return (T) this;
  }

  /**
   * @return alertLevel
   */
  public AlertLevel getAlertLevel() {
    return this.alertLevel;
  }

  /**
   * @param alertLevel alertLevel to set
   * @return AlertCreate
   */
  public <T extends AlertCreate> T setAlertLevel(AlertLevel alertLevel) {
    this.alertLevel = alertLevel;
    return (T) this;
  }

  /**
   * @return relatedId
   */
  public String getRelatedId() {
    return this.relatedId;
  }

  /**
   * @param relatedId relatedId to set
   * @return AlertCreate
   */
  public <T extends AlertCreate> T setRelatedId(String relatedId) {
    this.relatedId = relatedId;
    return (T) this;
  }

  /**
   * @return relatedType
   */
  public String getRelatedType() {
    return this.relatedType;
  }

  /**
   * @param relatedType relatedType to set
   * @return AlertCreate
   */
  public <T extends AlertCreate> T setRelatedType(String relatedType) {
    this.relatedType = relatedType;
    return (T) this;
  }

  public OffsetDateTime getHandledAt() {
    return handledAt;
  }

  public <T extends AlertCreate> T setHandledAt(OffsetDateTime handledAt) {
    this.handledAt = handledAt;
    return (T) this;
  }
}
