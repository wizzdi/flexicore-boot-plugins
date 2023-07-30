package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.BuildingFloor;

/** Object Used to Update BuildingFloor */
@IdValid.List({
  @IdValid(
      targetField = "buildingFloor",
      field = "id",
      fieldType = BuildingFloor.class,
      groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class BuildingFloorUpdate extends BuildingFloorCreate {

  @JsonIgnore private BuildingFloor buildingFloor;

  private String id;

  public BuildingFloor getBuildingFloor() {
    return buildingFloor;
  }

  public BuildingFloorUpdate setBuildingFloor(BuildingFloor buildingFloor) {
    this.buildingFloor = buildingFloor;
    return this;
  }

  public String getId() {
    return id;
  }

  public BuildingFloorUpdate setId(String id) {
    this.id = id;
    return this;
  }
}
