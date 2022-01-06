package com.wizzdi.flexicore.building.model;

import com.flexicore.model.SecuredBasic;
import com.wizzdi.maps.model.MappedPOI;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Building extends SecuredBasic {

  private String externalId;

  @ManyToOne(targetEntity = MappedPOI.class)
  private MappedPOI mappedPOI;

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
}
