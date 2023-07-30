package com.wizzdi.video.conference.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.video.conference.model.RoomToVideoServerUser;

public class RoomToVideoServerUserUpdate extends RoomToVideoServerUserCreate {

  private String id;
  @JsonIgnore private RoomToVideoServerUser roomToVideoServerUser;

  public String getId() {
    return id;
  }

  public <T extends RoomToVideoServerUserUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public RoomToVideoServerUser getRoomToVideoServerUser() {
    return roomToVideoServerUser;
  }

  public <T extends RoomToVideoServerUserUpdate> T setRoomToVideoServerUser(
      RoomToVideoServerUser roomToVideoServerUser) {
    this.roomToVideoServerUser = roomToVideoServerUser;
    return (T) this;
  }
}
