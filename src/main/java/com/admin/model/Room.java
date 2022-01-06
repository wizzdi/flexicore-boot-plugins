package com.admin.model;

import com.flexicore.model.SecuredBasic;
import javax.persistence.Entity;

@Entity
public class Room extends SecuredBasic {

  private String externalId;

  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return Room
   */
  public <T extends Room> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }
}
