package com.wizzdi.alerts;

import com.flexicore.model.Baseclass;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

import java.time.OffsetDateTime;

@Entity
public class Alert extends Baseclass {

  private AlertLevel alertLevel;

  @Lob
  private String alertContent;

  private String relatedId;

  private String relatedType;

  private String alertCategory;

  private OffsetDateTime handledAt;


  /**
   * @return alertLevel
   */
  public AlertLevel getAlertLevel() {
    return this.alertLevel;
  }

  /**
   * @param alertLevel alertLevel to set
   * @return Alert
   */
  public <T extends Alert> T setAlertLevel(AlertLevel alertLevel) {
    this.alertLevel = alertLevel;
    return (T) this;
  }

  /**
   * @return alertContent
   */
  @Column(columnDefinition = "text")
  public String getAlertContent() {
    return this.alertContent;
  }

  /**
   * @param alertContent alertContent to set
   * @return Alert
   */
  public <T extends Alert> T setAlertContent(String alertContent) {
    this.alertContent = alertContent;
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
   * @return Alert
   */
  public <T extends Alert> T setRelatedId(String relatedId) {
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
   * @return Alert
   */
  public <T extends Alert> T setRelatedType(String relatedType) {
    this.relatedType = relatedType;
    return (T) this;
  }

  /**
   * @return alertCategory
   */
  public String getAlertCategory() {
    return this.alertCategory;
  }

  /**
   * @param alertCategory alertCategory to set
   * @return Alert
   */
  public <T extends Alert> T setAlertCategory(String alertCategory) {
    this.alertCategory = alertCategory;
    return (T) this;
  }

  public OffsetDateTime getHandledAt() {
    return handledAt;
  }

  public <T extends Alert> T setHandledAt(OffsetDateTime handledAt) {
    this.handledAt = handledAt;
    return (T) this;
  }
}
