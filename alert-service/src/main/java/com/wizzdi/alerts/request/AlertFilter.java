package com.wizzdi.alerts.request;

import com.wizzdi.alerts.AlertLevel;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import java.util.Set;

/** Object Used to List Alert */
public class AlertFilter extends PaginationFilter {

  private Set<String> alertCategory;

  private Set<String> alertContent;

  private Set<AlertLevel> alertLevel;

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> relatedId;

  private Set<String> relatedType;

  private Boolean handled;

  /**
   * @return alertCategory
   */
  public Set<String> getAlertCategory() {
    return this.alertCategory;
  }

  /**
   * @param alertCategory alertCategory to set
   * @return AlertFilter
   */
  public <T extends AlertFilter> T setAlertCategory(Set<String> alertCategory) {
    this.alertCategory = alertCategory;
    return (T) this;
  }

  /**
   * @return alertContent
   */
  public Set<String> getAlertContent() {
    return this.alertContent;
  }

  /**
   * @param alertContent alertContent to set
   * @return AlertFilter
   */
  public <T extends AlertFilter> T setAlertContent(Set<String> alertContent) {
    this.alertContent = alertContent;
    return (T) this;
  }

  /**
   * @return alertLevel
   */
  public Set<AlertLevel> getAlertLevel() {
    return this.alertLevel;
  }

  /**
   * @param alertLevel alertLevel to set
   * @return AlertFilter
   */
  public <T extends AlertFilter> T setAlertLevel(Set<AlertLevel> alertLevel) {
    this.alertLevel = alertLevel;
    return (T) this;
  }

  /**
   * @return basicPropertiesFilter
   */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return AlertFilter
   */
  public <T extends AlertFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /**
   * @return relatedId
   */
  public Set<String> getRelatedId() {
    return this.relatedId;
  }

  /**
   * @param relatedId relatedId to set
   * @return AlertFilter
   */
  public <T extends AlertFilter> T setRelatedId(Set<String> relatedId) {
    this.relatedId = relatedId;
    return (T) this;
  }

  /**
   * @return relatedType
   */
  public Set<String> getRelatedType() {
    return this.relatedType;
  }

  /**
   * @param relatedType relatedType to set
   * @return AlertFilter
   */
  public <T extends AlertFilter> T setRelatedType(Set<String> relatedType) {
    this.relatedType = relatedType;
    return (T) this;
  }

  public Boolean getHandled() {
    return handled;
  }

  public <T extends AlertFilter> T setHandled(Boolean handled) {
    this.handled = handled;
    return (T) this;
  }
}
