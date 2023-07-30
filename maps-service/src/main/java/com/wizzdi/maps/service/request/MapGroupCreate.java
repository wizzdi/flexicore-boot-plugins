package com.wizzdi.maps.service.request;

import com.wizzdi.flexicore.security.request.BasicCreate;

/** Object Used to Create MapGroup */
public class MapGroupCreate extends BasicCreate {

  private String externalId;

  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return MapGroupCreate
   */
  public <T extends MapGroupCreate> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }
}
