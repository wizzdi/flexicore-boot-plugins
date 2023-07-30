package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MapGroup;

/** Object Used to Update MapGroup */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mapGroup",
      field = "id",
      fieldType = com.wizzdi.maps.model.MapGroup.class,
      groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class MapGroupUpdate extends MapGroupCreate {

  private String id;

  @JsonIgnore private MapGroup mapGroup;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return MapGroupUpdate
   */
  public <T extends MapGroupUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return mapGroup */
  @JsonIgnore
  public MapGroup getMapGroup() {
    return this.mapGroup;
  }

  /**
   * @param mapGroup mapGroup to set
   * @return MapGroupUpdate
   */
  public <T extends MapGroupUpdate> T setMapGroup(MapGroup mapGroup) {
    this.mapGroup = mapGroup;
    return (T) this;
  }
}
