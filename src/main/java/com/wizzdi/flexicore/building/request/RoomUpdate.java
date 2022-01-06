package com.wizzdi.flexicore.building.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.building.model.Room;

public class RoomUpdate extends RoomCreate {

  private String id;
  @JsonIgnore private Room room;

  public String getId() {
    return id;
  }

  public <T extends RoomUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public Room getRoom() {
    return room;
  }

  public <T extends RoomUpdate> T setRoom(Room room) {
    this.room = room;
    return (T) this;
  }
}
