package com.wizzdi.video.conference.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.video.conference.model.VideoServerUser;

public class VideoServerUserUpdate extends VideoServerUserCreate {

  private String id;
  @JsonIgnore private VideoServerUser videoServerUser;

  public String getId() {
    return id;
  }

  public <T extends VideoServerUserUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public VideoServerUser getVideoServerUser() {
    return videoServerUser;
  }

  public <T extends VideoServerUserUpdate> T setVideoServerUser(VideoServerUser videoServerUser) {
    this.videoServerUser = videoServerUser;
    return (T) this;
  }
}
