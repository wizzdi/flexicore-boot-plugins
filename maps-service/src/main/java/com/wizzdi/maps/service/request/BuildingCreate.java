package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MappedPOI;

/** Object Used to Create Building */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mappedPOI",
      field = "mappedPOIId",
      fieldType = com.wizzdi.maps.model.MappedPOI.class,
      groups = {
        com.wizzdi.flexicore.security.validation.Create.class,
        com.wizzdi.flexicore.security.validation.Update.class
      })
})
public class BuildingCreate extends BasicCreate {

  private String externalId;

  @JsonIgnore private MappedPOI mappedPOI;

  private String mappedPOIId;

  /** @return externalId */
  public String getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return BuildingCreate
   */
  public <T extends BuildingCreate> T setExternalId(String externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  /** @return mappedPOI */
  @JsonIgnore
  public MappedPOI getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return BuildingCreate
   */
  public <T extends BuildingCreate> T setMappedPOI(MappedPOI mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return mappedPOIId */
  public String getMappedPOIId() {
    return this.mappedPOIId;
  }

  /**
   * @param mappedPOIId mappedPOIId to set
   * @return BuildingCreate
   */
  public <T extends BuildingCreate> T setMappedPOIId(String mappedPOIId) {
    this.mappedPOIId = mappedPOIId;
    return (T) this;
  }
}
