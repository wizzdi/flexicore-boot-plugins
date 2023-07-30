package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Building;

/** Object Used to Update Building */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "building",
      field = "id",
      fieldType = com.wizzdi.maps.model.Building.class,
      groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class BuildingUpdate extends BuildingCreate {

  @JsonIgnore private Building building;

  private String id;

  /** @return building */
  @JsonIgnore
  public Building getBuilding() {
    return this.building;
  }

  /**
   * @param building building to set
   * @return BuildingUpdate
   */
  public <T extends BuildingUpdate> T setBuilding(Building building) {
    this.building = building;
    return (T) this;
  }

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return BuildingUpdate
   */
  public <T extends BuildingUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }
}
