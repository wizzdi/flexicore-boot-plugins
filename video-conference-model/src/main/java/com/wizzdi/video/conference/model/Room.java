package com.wizzdi.video.conference.model;

import com.flexicore.model.SecuredBasic;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Room extends SecuredBasic {

  @ManyToOne(targetEntity = VideoServer.class)
  private VideoServer videoServer;

  /** @return videoServer */
  @ManyToOne(targetEntity = VideoServer.class)
  public VideoServer getVideoServer() {
    return this.videoServer;
  }

  /**
   * @param videoServer videoServer to set
   * @return Room
   */
  public <T extends Room> T setVideoServer(VideoServer videoServer) {
    this.videoServer = videoServer;
    return (T) this;
  }
}
