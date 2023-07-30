package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.BuildingFloor;

/** Object Used to Create Room */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "buildingFloor",
      field = "buildingFloorId",
      fieldType = com.wizzdi.maps.model.BuildingFloor.class,
      groups = {
        com.wizzdi.flexicore.security.validation.Update.class,
        com.wizzdi.flexicore.security.validation.Create.class
      })
})
public class RoomCreate extends BasicCreate {

  @JsonIgnore private BuildingFloor buildingFloor;

  private String buildingFloorId;

  private String externalId;

  private Double x;

  private Double y;

  private Double z;


  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return RoomCreate
   */
  public <T extends RoomCreate> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  /** @return x */
  public Double getX() {
    return this.x;
  }

  /**
   * @param x x to set
   * @return RoomCreate
   */
  public <T extends RoomCreate> T setX(Double x) {
    this.x = x;
    return (T) this;
  }

  /** @return y */
  public Double getY() {
    return this.y;
  }

  /**
   * @param y y to set
   * @return RoomCreate
   */
  public <T extends RoomCreate> T setY(Double y) {
    this.y = y;
    return (T) this;
  }

  /** @return z */
  public Double getZ() {
    return this.z;
  }

  /**
   * @param z z to set
   * @return RoomCreate
   */
  public <T extends RoomCreate> T setZ(Double z) {
    this.z = z;
    return (T) this;
  }

  public BuildingFloor getBuildingFloor() {
    return buildingFloor;
  }

  public RoomCreate setBuildingFloor(BuildingFloor buildingFloor) {
    this.buildingFloor = buildingFloor;
    return this;
  }

  public String getBuildingFloorId() {
    return buildingFloorId;
  }

  public RoomCreate setBuildingFloorId(String buildingFloorId) {
    this.buildingFloorId = buildingFloorId;
    return this;
  }
}
