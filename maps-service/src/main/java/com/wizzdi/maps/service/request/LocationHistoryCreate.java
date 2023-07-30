package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.Room;

import java.time.OffsetDateTime;

/** Object Used to Create LocationHistory */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "room",
      field = "roomId",
      fieldType = com.wizzdi.maps.model.Room.class,
      groups = {
        com.wizzdi.flexicore.security.validation.Create.class,
        com.wizzdi.flexicore.security.validation.Update.class
      }),
  @IdValid(
      targetField = "mappedPOI",
      field = "mappedPOIId",
      fieldType = com.wizzdi.maps.model.MappedPOI.class,
      groups = {
        com.wizzdi.flexicore.security.validation.Update.class,
        com.wizzdi.flexicore.security.validation.Create.class
      })
})
public class LocationHistoryCreate extends BasicCreate {
    private OffsetDateTime dateAtLocation;
    private Double y;
    private Double z;
    @JsonIgnore
    private Room room;
    @JsonIgnore
    private MappedPOI mappedPOI;
    private Double x;
    private String roomId;
    private String mappedPOIId;
    private Double lon;
    private Double lat;

    public LocationHistoryCreate(MappedPOI other) {
        this(other, OffsetDateTime.now());
    }

    public LocationHistoryCreate(MappedPOI other, OffsetDateTime dateAtLocation) {
        this.dateAtLocation = dateAtLocation;
        this.y = other.getY();
        this.z = other.getZ();
        this.room = other.getRoom();
        this.mappedPOI = other;
        this.x = other.getX();
        this.lon = other.getLon();
        this.lat = other.getLat();
    }

    public LocationHistoryCreate() {
    }

    public OffsetDateTime getDateAtLocation() {
        return this.dateAtLocation;
    }

    public <T extends LocationHistoryCreate> T setDateAtLocation(OffsetDateTime dateAtLocation) {
        this.dateAtLocation = dateAtLocation;
        return (T) this;
    }

    public Double getY() {
        return this.y;
    }

    public <T extends LocationHistoryCreate> T setY(Double y) {
        this.y = y;
        return (T) this;
    }

    public Double getZ() {
        return this.z;
    }

    public <T extends LocationHistoryCreate> T setZ(Double z) {
        this.z = z;
        return (T) this;
    }

    @JsonIgnore
    public Room getRoom() {
        return this.room;
    }

    public <T extends LocationHistoryCreate> T setRoom(Room room) {
        this.room = room;
        return (T) this;
    }

    @JsonIgnore
    public MappedPOI getMappedPOI() {
        return this.mappedPOI;
    }

    public <T extends LocationHistoryCreate> T setMappedPOI(MappedPOI mappedPOI) {
        this.mappedPOI = mappedPOI;
        return (T) this;
    }

    public Double getX() {
        return this.x;
    }

    public <T extends LocationHistoryCreate> T setX(Double x) {
        this.x = x;
        return (T) this;
    }

    public String getRoomId() {
        return this.roomId;
    }

    public <T extends LocationHistoryCreate> T setRoomId(String roomId) {
        this.roomId = roomId;
        return (T) this;
    }

    public String getMappedPOIId() {
        return this.mappedPOIId;
    }

    public <T extends LocationHistoryCreate> T setMappedPOIId(String mappedPOIId) {
        this.mappedPOIId = mappedPOIId;
        return (T) this;
    }

    public Double getLon() {
        return this.lon;
    }

    public <T extends LocationHistoryCreate> T setLon(Double lon) {
        this.lon = lon;
        return (T) this;
    }

    public Double getLat() {
        return this.lat;
    }

    public <T extends LocationHistoryCreate> T setLat(Double lat) {
        this.lat = lat;
        return (T) this;
    }
}
