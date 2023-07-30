package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.StatusHistory;

/** Object Used to Update StatusHistory */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "statusHistory",
      field = "id",
      fieldType = com.wizzdi.maps.model.StatusHistory.class,
      groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class StatusHistoryUpdate extends StatusHistoryCreate {

  private String id;

  @JsonIgnore private StatusHistory statusHistory;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return StatusHistoryUpdate
   */
  public <T extends StatusHistoryUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return statusHistory */
  @JsonIgnore
  public StatusHistory getStatusHistory() {
    return this.statusHistory;
  }

  /**
   * @param statusHistory statusHistory to set
   * @return StatusHistoryUpdate
   */
  public <T extends StatusHistoryUpdate> T setStatusHistory(StatusHistory statusHistory) {
    this.statusHistory = statusHistory;
    return (T) this;
  }
}
