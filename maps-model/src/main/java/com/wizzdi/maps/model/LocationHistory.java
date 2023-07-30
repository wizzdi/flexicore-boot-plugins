package com.wizzdi.maps.model;

import com.flexicore.model.SecuredBasic;
import java.time.OffsetDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "location_history_idx",columnList = "mappedPOI_id,lat,lon,dateAtLocation")
})
public class LocationHistory extends SecuredBasic {

  private OffsetDateTime dateAtLocation;

  private Double y;

  private Double z;

  @ManyToOne(targetEntity = Room.class)
  private Room room;

  @ManyToOne(targetEntity = MappedPOI.class)
  private MappedPOI mappedPOI;

  private Double x;

  private Double lon;

  private Double lat;

  /** @return dateAtLocation */
  public OffsetDateTime getDateAtLocation() {
    return this.dateAtLocation;
  }

  /**
   * @param dateAtLocation dateAtLocation to set
   * @return LocationHistory
   */
  public <T extends LocationHistory> T setDateAtLocation(OffsetDateTime dateAtLocation) {
    this.dateAtLocation = dateAtLocation;
    return (T) this;
  }

  /** @return y */
  public Double getY() {
    return this.y;
  }

  /**
   * @param y y to set
   * @return LocationHistory
   */
  public <T extends LocationHistory> T setY(Double y) {
    this.y = y;
    return (T) this;
  }

  /** @return z */
  public Double getZ() {
    return this.z;
  }

  /**
   * @param z z to set
   * @return LocationHistory
   */
  public <T extends LocationHistory> T setZ(Double z) {
    this.z = z;
    return (T) this;
  }

  /** @return room */
  @ManyToOne(targetEntity = Room.class)
  public Room getRoom() {
    return this.room;
  }

  /**
   * @param room room to set
   * @return LocationHistory
   */
  public <T extends LocationHistory> T setRoom(Room room) {
    this.room = room;
    return (T) this;
  }

  /** @return mappedPOI */
  @ManyToOne(targetEntity = MappedPOI.class)
  public MappedPOI getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return LocationHistory
   */
  public <T extends LocationHistory> T setMappedPOI(MappedPOI mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return x */
  public Double getX() {
    return this.x;
  }

  /**
   * @param x x to set
   * @return LocationHistory
   */
  public <T extends LocationHistory> T setX(Double x) {
    this.x = x;
    return (T) this;
  }

  /** @return lon */
  public Double getLon() {
    return this.lon;
  }

  /**
   * @param lon lon to set
   * @return LocationHistory
   */
  public <T extends LocationHistory> T setLon(Double lon) {
    this.lon = lon;
    return (T) this;
  }

  /** @return lat */
  public Double getLat() {
    return this.lat;
  }

  /**
   * @param lat lat to set
   * @return LocationHistory
   */
  public <T extends LocationHistory> T setLat(Double lat) {
    this.lat = lat;
    return (T) this;
  }
}
