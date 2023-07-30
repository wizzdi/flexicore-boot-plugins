package com.wizzdi.maps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.SecuredBasic;
import java.util.List;
import jakarta.persistence.*;

@Entity
@Table(indexes = {
        @Index(name = "room_idx",columnList = "building_id")
})
public class Room extends SecuredBasic {

  @ManyToOne(targetEntity = Building.class)
  private Building building;

  @OneToMany(targetEntity = LocationHistory.class, mappedBy = "room")
  @JsonIgnore
  private List<LocationHistory> roomLocationHistories;

  @OneToMany(targetEntity = MappedPOI.class, mappedBy = "room")
  @JsonIgnore
  private List<MappedPOI> roomMappedPOIs;

  private Double z;

  private Double x;

  private String externalId;

  private Double y;

  /** @return building */
  @ManyToOne(targetEntity = Building.class)
  public Building getBuilding() {
    return this.building;
  }

  /**
   * @param building building to set
   * @return Room
   */
  public <T extends Room> T setBuilding(Building building) {
    this.building = building;
    return (T) this;
  }

  /** @return roomLocationHistories */
  @OneToMany(targetEntity = LocationHistory.class, mappedBy = "room")
  @JsonIgnore
  public List<LocationHistory> getRoomLocationHistories() {
    return this.roomLocationHistories;
  }

  /**
   * @param roomLocationHistories roomLocationHistories to set
   * @return Room
   */
  public <T extends Room> T setRoomLocationHistories(List<LocationHistory> roomLocationHistories) {
    this.roomLocationHistories = roomLocationHistories;
    return (T) this;
  }

  /** @return roomMappedPOIs */
  @OneToMany(targetEntity = MappedPOI.class, mappedBy = "room")
  @JsonIgnore
  public List<MappedPOI> getRoomMappedPOIs() {
    return this.roomMappedPOIs;
  }

  /**
   * @param roomMappedPOIs roomMappedPOIs to set
   * @return Room
   */
  public <T extends Room> T setRoomMappedPOIs(List<MappedPOI> roomMappedPOIs) {
    this.roomMappedPOIs = roomMappedPOIs;
    return (T) this;
  }

  /** @return z */
  public Double getZ() {
    return this.z;
  }

  /**
   * @param z z to set
   * @return Room
   */
  public <T extends Room> T setZ(Double z) {
    this.z = z;
    return (T) this;
  }

  /** @return x */
  public Double getX() {
    return this.x;
  }

  /**
   * @param x x to set
   * @return Room
   */
  public <T extends Room> T setX(Double x) {
    this.x = x;
    return (T) this;
  }

  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return Room
   */
  public <T extends Room> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  /** @return y */
  public Double getY() {
    return this.y;
  }

  /**
   * @param y y to set
   * @return Room
   */
  public <T extends Room> T setY(Double y) {
    this.y = y;
    return (T) this;
  }
}
