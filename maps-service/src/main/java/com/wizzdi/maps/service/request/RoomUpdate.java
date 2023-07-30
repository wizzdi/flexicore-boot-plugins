package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Room;

/** Object Used to Update Room */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "room",
      field = "id",
      fieldType = com.wizzdi.maps.model.Room.class,
      groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class RoomUpdate extends RoomCreate {

  private String id;

  @JsonIgnore private Room room;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return RoomUpdate
   */
  public <T extends RoomUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return room */
  @JsonIgnore
  public Room getRoom() {
    return this.room;
  }

  /**
   * @param room room to set
   * @return RoomUpdate
   */
  public <T extends RoomUpdate> T setRoom(Room room) {
    this.room = room;
    return (T) this;
  }
}
