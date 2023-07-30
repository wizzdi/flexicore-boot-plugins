package com.wizzdi.video.conference.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.video.conference.model.Room;
import com.wizzdi.video.conference.model.VideoServerUser;

public class RoomToVideoServerUserCreate extends BasicCreate {

  @JsonIgnore private Room room;

  private String videoServerUserId;

  private String roomUrl;

  @JsonIgnore private VideoServerUser videoServerUser;

  private String roomId;

  @JsonIgnore
  public Room getRoom() {
    return this.room;
  }

  public <T extends RoomToVideoServerUserCreate> T setRoom(Room room) {
    this.room = room;
    return (T) this;
  }

  public String getVideoServerUserId() {
    return this.videoServerUserId;
  }

  public <T extends RoomToVideoServerUserCreate> T setVideoServerUserId(String videoServerUserId) {
    this.videoServerUserId = videoServerUserId;
    return (T) this;
  }

  public String getRoomUrl() {
    return this.roomUrl;
  }

  public <T extends RoomToVideoServerUserCreate> T setRoomUrl(String roomUrl) {
    this.roomUrl = roomUrl;
    return (T) this;
  }

  @JsonIgnore
  public VideoServerUser getVideoServerUser() {
    return this.videoServerUser;
  }

  public <T extends RoomToVideoServerUserCreate> T setVideoServerUser(
      VideoServerUser videoServerUser) {
    this.videoServerUser = videoServerUser;
    return (T) this;
  }

  public String getRoomId() {
    return this.roomId;
  }

  public <T extends RoomToVideoServerUserCreate> T setRoomId(String roomId) {
    this.roomId = roomId;
    return (T) this;
  }
}
