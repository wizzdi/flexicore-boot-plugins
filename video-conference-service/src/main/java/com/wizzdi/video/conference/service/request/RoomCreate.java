package com.wizzdi.video.conference.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.video.conference.model.VideoServer;

public class RoomCreate extends BasicCreate {

  private String videoServerId;

  @JsonIgnore private VideoServer videoServer;

  public String getVideoServerId() {
    return this.videoServerId;
  }

  public <T extends RoomCreate> T setVideoServerId(String videoServerId) {
    this.videoServerId = videoServerId;
    return (T) this;
  }

  @JsonIgnore
  public VideoServer getVideoServer() {
    return this.videoServer;
  }

  public <T extends RoomCreate> T setVideoServer(VideoServer videoServer) {
    this.videoServer = videoServer;
    return (T) this;
  }
}
