package com.wizzdi.video.conference.model;

import com.flexicore.model.Baseclass;
import jakarta.persistence.Entity;

@Entity
public class VideoServer extends Baseclass {

  private String baseUrl;

  /** @return baseUrl */
  public String getBaseUrl() {
    return this.baseUrl;
  }

  /**
   * @param baseUrl baseUrl to set
   * @return VideoServer
   */
  public <T extends VideoServer> T setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return (T) this;
  }
}
