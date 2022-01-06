package com.admin.service.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

public class RoomCreate extends BasicCreate {

  private String externalId;

  public String getExternalId() {
    return this.externalId;
  }

  public <T extends RoomCreate> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }
}
