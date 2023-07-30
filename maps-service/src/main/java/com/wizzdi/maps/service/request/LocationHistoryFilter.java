package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.Room;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/** Object Used to List LocationHistory */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(targetField = "room", field = "roomIds", fieldType = com.wizzdi.maps.model.Room.class),
  @IdValid(
      targetField = "mappedPOI",
      field = "mappedPOIIds",
      fieldType = com.wizzdi.maps.model.MappedPOI.class)
})
public class LocationHistoryFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<OffsetDateTime> dateAtLocation;

  private Set<Double> lat;

  private Set<Double> lon;

  @JsonIgnore private List<MappedPOI> mappedPOI;

  private Set<String> mappedPOIIds;

  @JsonIgnore private List<Room> room;

  private Set<String> roomIds;

  private Set<Double> x;

  private Set<Double> y;

  private Set<Double> z;

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return dateAtLocation */
  public Set<OffsetDateTime> getDateAtLocation() {
    return this.dateAtLocation;
  }

  /**
   * @param dateAtLocation dateAtLocation to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setDateAtLocation(Set<OffsetDateTime> dateAtLocation) {
    this.dateAtLocation = dateAtLocation;
    return (T) this;
  }

  /** @return lat */
  public Set<Double> getLat() {
    return this.lat;
  }

  /**
   * @param lat lat to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setLat(Set<Double> lat) {
    this.lat = lat;
    return (T) this;
  }

  /** @return lon */
  public Set<Double> getLon() {
    return this.lon;
  }

  /**
   * @param lon lon to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setLon(Set<Double> lon) {
    this.lon = lon;
    return (T) this;
  }

  /** @return mappedPOI */
  @JsonIgnore
  public List<MappedPOI> getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setMappedPOI(List<MappedPOI> mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return mappedPOIIds */
  public Set<String> getMappedPOIIds() {
    return this.mappedPOIIds;
  }

  /**
   * @param mappedPOIIds mappedPOIIds to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setMappedPOIIds(Set<String> mappedPOIIds) {
    this.mappedPOIIds = mappedPOIIds;
    return (T) this;
  }

  /** @return room */
  @JsonIgnore
  public List<Room> getRoom() {
    return this.room;
  }

  /**
   * @param room room to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setRoom(List<Room> room) {
    this.room = room;
    return (T) this;
  }

  /** @return roomIds */
  public Set<String> getRoomIds() {
    return this.roomIds;
  }

  /**
   * @param roomIds roomIds to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setRoomIds(Set<String> roomIds) {
    this.roomIds = roomIds;
    return (T) this;
  }

  /** @return x */
  public Set<Double> getX() {
    return this.x;
  }

  /**
   * @param x x to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setX(Set<Double> x) {
    this.x = x;
    return (T) this;
  }

  /** @return y */
  public Set<Double> getY() {
    return this.y;
  }

  /**
   * @param y y to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setY(Set<Double> y) {
    this.y = y;
    return (T) this;
  }

  /** @return z */
  public Set<Double> getZ() {
    return this.z;
  }

  /**
   * @param z z to set
   * @return LocationHistoryFilter
   */
  public <T extends LocationHistoryFilter> T setZ(Set<Double> z) {
    this.z = z;
    return (T) this;
  }
}
