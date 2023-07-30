package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MapGroupToMappedPOI;

/** Object Used to Update MapGroupToMappedPOI */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mapGroupToMappedPOI",
      field = "id",
      fieldType = com.wizzdi.maps.model.MapGroupToMappedPOI.class,
      groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class MapGroupToMappedPOIUpdate extends MapGroupToMappedPOICreate {

  private String id;

  @JsonIgnore private MapGroupToMappedPOI mapGroupToMappedPOI;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return MapGroupToMappedPOIUpdate
   */
  public <T extends MapGroupToMappedPOIUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return mapGroupToMappedPOI */
  @JsonIgnore
  public MapGroupToMappedPOI getMapGroupToMappedPOI() {
    return this.mapGroupToMappedPOI;
  }

  /**
   * @param mapGroupToMappedPOI mapGroupToMappedPOI to set
   * @return MapGroupToMappedPOIUpdate
   */
  public <T extends MapGroupToMappedPOIUpdate> T setMapGroupToMappedPOI(
      MapGroupToMappedPOI mapGroupToMappedPOI) {
    this.mapGroupToMappedPOI = mapGroupToMappedPOI;
    return (T) this;
  }
}
