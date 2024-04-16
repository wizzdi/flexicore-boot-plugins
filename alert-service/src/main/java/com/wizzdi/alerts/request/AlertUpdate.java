package com.wizzdi.alerts.request;

import com.wizzdi.alerts.Alert;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.flexicore.security.validation.Update;

/** Object Used to Update Alert */
@IdValid.List({
  @IdValid(
      targetField = "alert",
      field = "id",
      fieldType = Alert.class,
      groups = {Update.class})
})
public class AlertUpdate extends AlertCreate {

  @JsonIgnore private Alert alert;

  private String id;

  /**
   * @return alert
   */
  @JsonIgnore
  public Alert getAlert() {
    return this.alert;
  }

  /**
   * @param alert alert to set
   * @return AlertUpdate
   */
  public <T extends AlertUpdate> T setAlert(Alert alert) {
    this.alert = alert;
    return (T) this;
  }

  /**
   * @return id
   */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return AlertUpdate
   */
  public <T extends AlertUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }
}
