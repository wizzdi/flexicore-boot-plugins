package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.model.territories.Address;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.Room;

public class MappedPOICreate extends BasicCreate {

  private String mapIconId;
  private Double y;
  private Double z;
  private Double lat;
  private Double x;
  private String addressId;
  @JsonIgnore
  private Address address;
  @JsonIgnore
  private MapIcon mapIcon;
  private Double lon;
  @JsonIgnore
  private Room room;
  private String roomId;
  private String externalId;
  private Boolean keepLocationHistory;
  private String relatedType;
  private String relatedId;
  private Boolean keepStatusHistory;




  public String getMapIconId() {
    return this.mapIconId;
  }

  public <T extends MappedPOICreate> T setMapIconId(String mapIconId) {
    this.mapIconId = mapIconId;
    return (T) this;
  }

  public Double getY() {
    return y;
  }

  public <T extends MappedPOICreate> T setY(Double y) {
    this.y = y;
    return (T) this;
  }

  public Double getZ() {
    return z;
  }

  public <T extends MappedPOICreate> T setZ(Double z) {
    this.z = z;
    return (T) this;
  }

  public Double getLat() {
    return lat;
  }

  public <T extends MappedPOICreate> T setLat(Double lat) {
    this.lat = lat;
    return (T) this;
  }

  public Double getX() {
    return x;
  }

  public <T extends MappedPOICreate> T setX(Double x) {
    this.x = x;
    return (T) this;
  }

  @JsonIgnore
  public Room getRoom() {
    return this.room;
  }

  public <T extends MappedPOICreate> T setRoom(Room room) {
    this.room = room;
    return (T) this;
  }

  public String getRoomId() {
    return this.roomId;
  }

  public <T extends MappedPOICreate> T setRoomId(String roomId) {
    this.roomId = roomId;
    return (T) this;
  }

  @JsonIgnore
  public Address getAddress() {
    return address;
  }

  public <T extends MappedPOICreate> T setAddress(Address address) {
    this.address = address;
    return (T) this;
  }

  @JsonIgnore
  public MapIcon getMapIcon() {
    return this.mapIcon;
  }

  public <T extends MappedPOICreate> T setMapIcon(MapIcon mapIcon) {
    this.mapIcon = mapIcon;
    return (T) this;
  }

  public Double getLon() {
    return lon;
  }

  public <T extends MappedPOICreate> T setLon(Double lon) {
    this.lon = lon;
    return (T) this;
  }

  public String getAddressId() {
    return addressId;
  }

  public <T extends MappedPOICreate> T setAddressId(String addressId) {
    this.addressId = addressId;
    return (T) this;
  }
  public String getExternalId() {
    return this.externalId;
  }

  public <T extends MappedPOICreate> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }


  /** @return relatedType */
  public String getRelatedType() {
    return this.relatedType;
  }

  /**
   * @param relatedType relatedType to set
   * @return MappedPOICreate
   */
  public <T extends MappedPOICreate> T setRelatedType(String relatedType) {
    this.relatedType = relatedType;
    return (T) this;
  }

  /** @return relatedId */
  public String getRelatedId() {
    return this.relatedId;
  }

  /**
   * @param relatedId relatedId to set
   * @return MappedPOICreate
   */
  public <T extends MappedPOICreate> T setRelatedId(String relatedId) {
    this.relatedId = relatedId;
    return (T) this;
  }
  /** @return keepStatusHistory */
  public Boolean getKeepStatusHistory() {
    return this.keepStatusHistory;
  }

  /**
   * @param keepStatusHistory keepStatusHistory to set
   * @return MappedPOICreate
   */
  public <T extends MappedPOICreate> T setKeepStatusHistory(Boolean keepStatusHistory) {
    this.keepStatusHistory = keepStatusHistory;
    return (T) this;
  }

  /** @return keepLocationHistory */
  public Boolean getKeepLocationHistory() {
    return this.keepLocationHistory;
  }

  /**
   * @param keepLocationHistory keepLocationHistory to set
   * @return MappedPOICreate
   */
  public <T extends MappedPOICreate> T setKeepLocationHistory(Boolean keepLocationHistory) {
    this.keepLocationHistory = keepLocationHistory;
    return (T) this;
  }


}
