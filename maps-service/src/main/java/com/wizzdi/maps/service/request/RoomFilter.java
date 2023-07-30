package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.BuildingFloor;
import com.wizzdi.maps.model.LocationHistory;
import com.wizzdi.maps.model.MappedPOI;
import java.util.List;
import java.util.Set;

/** Object Used to List Room */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "buildingFloors",
      field = "buildingFloorIds",
      fieldType = com.wizzdi.maps.model.BuildingFloor.class),
  @IdValid(
      targetField = "roomMappedPOIs",
      field = "roomMappedPOIsIds",
      fieldType = com.wizzdi.maps.model.MappedPOI.class),
  @IdValid(
      targetField = "roomLocationHistories",
      field = "roomLocationHistoriesIds",
      fieldType = com.wizzdi.maps.model.LocationHistory.class)
})
public class RoomFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;

  @JsonIgnore private List<BuildingFloor> buildingFloors;

  private Set<String> buildingFloorIds;

  private Set<String> externalId;

  @JsonIgnore private List<LocationHistory> roomLocationHistories;

  private Set<String> roomLocationHistoriesIds;

  @JsonIgnore private List<MappedPOI> roomMappedPOIs;

  private Set<String> roomMappedPOIsIds;

  private Set<Double> x;

  private Set<Double> y;

  private Set<Double> z;

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  public List<BuildingFloor> getBuildingFloors() {
    return buildingFloors;
  }

  public RoomFilter setBuildingFloors(List<BuildingFloor> buildingFloors) {
    this.buildingFloors = buildingFloors;
    return this;
  }

  public Set<String> getBuildingFloorIds() {
    return buildingFloorIds;
  }

  public RoomFilter setBuildingFloorIds(Set<String> buildingFloorIds) {
    this.buildingFloorIds = buildingFloorIds;
    return this;
  }

  /** @return externalId */
  public Set<String> getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setExternalId(Set<String> externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  /** @return roomLocationHistories */
  @JsonIgnore
  public List<LocationHistory> getRoomLocationHistories() {
    return this.roomLocationHistories;
  }

  /**
   * @param roomLocationHistories roomLocationHistories to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setRoomLocationHistories(
      List<LocationHistory> roomLocationHistories) {
    this.roomLocationHistories = roomLocationHistories;
    return (T) this;
  }

  /** @return roomLocationHistoriesIds */
  public Set<String> getRoomLocationHistoriesIds() {
    return this.roomLocationHistoriesIds;
  }

  /**
   * @param roomLocationHistoriesIds roomLocationHistoriesIds to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setRoomLocationHistoriesIds(
      Set<String> roomLocationHistoriesIds) {
    this.roomLocationHistoriesIds = roomLocationHistoriesIds;
    return (T) this;
  }

  /** @return roomMappedPOIs */
  @JsonIgnore
  public List<MappedPOI> getRoomMappedPOIs() {
    return this.roomMappedPOIs;
  }

  /**
   * @param roomMappedPOIs roomMappedPOIs to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setRoomMappedPOIs(List<MappedPOI> roomMappedPOIs) {
    this.roomMappedPOIs = roomMappedPOIs;
    return (T) this;
  }

  /** @return roomMappedPOIsIds */
  public Set<String> getRoomMappedPOIsIds() {
    return this.roomMappedPOIsIds;
  }

  /**
   * @param roomMappedPOIsIds roomMappedPOIsIds to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setRoomMappedPOIsIds(Set<String> roomMappedPOIsIds) {
    this.roomMappedPOIsIds = roomMappedPOIsIds;
    return (T) this;
  }

  /** @return x */
  public Set<Double> getX() {
    return this.x;
  }

  /**
   * @param x x to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setX(Set<Double> x) {
    this.x = x;
    return (T) this;
  }

  /** @return y */
  public Set<Double> getY() {
    return this.y;
  }

  /**
   * @param y y to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setY(Set<Double> y) {
    this.y = y;
    return (T) this;
  }

  /** @return z */
  public Set<Double> getZ() {
    return this.z;
  }

  /**
   * @param z z to set
   * @return RoomFilter
   */
  public <T extends RoomFilter> T setZ(Set<Double> z) {
    this.z = z;
    return (T) this;
  }
}
