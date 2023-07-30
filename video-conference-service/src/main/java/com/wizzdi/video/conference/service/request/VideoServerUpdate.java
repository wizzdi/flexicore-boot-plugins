package com.wizzdi.video.conference.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.video.conference.model.VideoServer;

public class VideoServerUpdate extends VideoServerCreate {

  private String id;
  @JsonIgnore private VideoServer videoServer;

  public String getId() {
    return id;
  }

  public <T extends VideoServerUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  @JsonIgnore
  public VideoServer getVideoServer() {
    return videoServer;
  }

  public <T extends VideoServerUpdate> T setVideoServer(VideoServer videoServer) {
    this.videoServer = videoServer;
    return (T) this;
  }
}
