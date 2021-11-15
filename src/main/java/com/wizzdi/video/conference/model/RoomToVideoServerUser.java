package com.wizzdi.video.conference.model;

import com.flexicore.model.SecuredBasic;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class RoomToVideoServerUser extends SecuredBasic {

  @ManyToOne(targetEntity = Room.class)
  private Room room;

  private String roomUrl;

  @ManyToOne(targetEntity = VideoServerUser.class)
  private VideoServerUser videoServerUser;

  /** @return room */
  @ManyToOne(targetEntity = Room.class)
  public Room getRoom() {
    return this.room;
  }

  /**
   * @param room room to set
   * @return RoomToVideoServerUser
   */
  public <T extends RoomToVideoServerUser> T setRoom(Room room) {
    this.room = room;
    return (T) this;
  }

  /** @return roomUrl */
  public String getRoomUrl() {
    return this.roomUrl;
  }

  /**
   * @param roomUrl roomUrl to set
   * @return RoomToVideoServerUser
   */
  public <T extends RoomToVideoServerUser> T setRoomUrl(String roomUrl) {
    this.roomUrl = roomUrl;
    return (T) this;
  }

  /** @return videoServerUser */
  @ManyToOne(targetEntity = VideoServerUser.class)
  public VideoServerUser getVideoServerUser() {
    return this.videoServerUser;
  }

  /**
   * @param videoServerUser videoServerUser to set
   * @return RoomToVideoServerUser
   */
  public <T extends RoomToVideoServerUser> T setVideoServerUser(VideoServerUser videoServerUser) {
    this.videoServerUser = videoServerUser;
    return (T) this;
  }
}
