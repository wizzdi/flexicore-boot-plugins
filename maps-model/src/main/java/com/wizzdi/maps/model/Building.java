package com.wizzdi.maps.model;

import com.flexicore.model.SecuredBasic;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class Building extends SecuredBasic {

  @ManyToOne(targetEntity = MappedPOI.class)
  private MappedPOI mappedPOI;

  private String externalId;

  /** @return mappedPOI */
  @ManyToOne(targetEntity = MappedPOI.class)
  public MappedPOI getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return Building
   */
  public <T extends Building> T setMappedPOI(MappedPOI mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return Building
   */
  public <T extends Building> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }
}
