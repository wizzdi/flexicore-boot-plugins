package com.wizzdi.video.conference.service.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class VideoServerCreate extends BasicCreate {

  private String baseUrl;

  public String getBaseUrl() {
    return this.baseUrl;
  }

  public <T extends VideoServerCreate> T setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return (T) this;
  }
}
