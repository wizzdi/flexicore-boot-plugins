package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.LocationHistory;

/** Object Used to Update LocationHistory */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "locationHistory",
      field = "id",
      fieldType = com.wizzdi.maps.model.LocationHistory.class,
      groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class LocationHistoryUpdate extends LocationHistoryCreate {

  private String id;

  @JsonIgnore private LocationHistory locationHistory;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return LocationHistoryUpdate
   */
  public <T extends LocationHistoryUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return locationHistory */
  @JsonIgnore
  public LocationHistory getLocationHistory() {
    return this.locationHistory;
  }

  /**
   * @param locationHistory locationHistory to set
   * @return LocationHistoryUpdate
   */
  public <T extends LocationHistoryUpdate> T setLocationHistory(LocationHistory locationHistory) {
    this.locationHistory = locationHistory;
    return (T) this;
  }
}
