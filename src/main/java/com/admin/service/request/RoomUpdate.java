package com.admin.service.request;

import com.admin.model.Room;
import com.fasterxml.jackson.annotation.JsonIgnore;

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
