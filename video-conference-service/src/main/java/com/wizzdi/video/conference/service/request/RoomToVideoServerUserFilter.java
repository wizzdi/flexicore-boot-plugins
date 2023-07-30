package com.wizzdi.video.conference.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.video.conference.model.Room;
import com.wizzdi.video.conference.model.VideoServerUser;
import java.util.List;
import java.util.Set;

public class RoomToVideoServerUserFilter extends PaginationFilter {

  @JsonIgnore private List<Room> room;

  private Set<String> videoServerUserIds;

  private Set<String> roomUrl;

  private BasicPropertiesFilter basicPropertiesFilter;

  @JsonIgnore private List<VideoServerUser> videoServerUser;

  private Set<String> roomIds;

  @JsonIgnore
  public List<Room> getRoom() {
    return this.room;
  }

  public <T extends RoomToVideoServerUserFilter> T setRoom(List<Room> room) {
    this.room = room;
    return (T) this;
  }

  public Set<String> getVideoServerUserIds() {
    return this.videoServerUserIds;
  }

  public <T extends RoomToVideoServerUserFilter> T setVideoServerUserIds(
      Set<String> videoServerUserIds) {
    this.videoServerUserIds = videoServerUserIds;
    return (T) this;
  }

  public Set<String> getRoomUrl() {
    return this.roomUrl;
  }

  public <T extends RoomToVideoServerUserFilter> T setRoomUrl(Set<String> roomUrl) {
    this.roomUrl = roomUrl;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends RoomToVideoServerUserFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  @JsonIgnore
  public List<VideoServerUser> getVideoServerUser() {
    return this.videoServerUser;
  }

  public <T extends RoomToVideoServerUserFilter> T setVideoServerUser(
      List<VideoServerUser> videoServerUser) {
    this.videoServerUser = videoServerUser;
    return (T) this;
  }

  public Set<String> getRoomIds() {
    return this.roomIds;
  }

  public <T extends RoomToVideoServerUserFilter> T setRoomIds(Set<String> roomIds) {
    this.roomIds = roomIds;
    return (T) this;
  }
}
