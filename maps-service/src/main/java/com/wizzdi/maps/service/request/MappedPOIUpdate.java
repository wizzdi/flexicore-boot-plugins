package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MappedPOI;

/** Object Used to Update MappedPOI */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mappedPOI",
      field = "id",
      fieldType = com.wizzdi.maps.model.MappedPOI.class,
      groups = {com.wizzdi.flexicore.security.validation.Update.class})
})
public class MappedPOIUpdate extends MappedPOICreate {

  private String id;

  @JsonIgnore private MappedPOI mappedPOI;

  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return MappedPOIUpdate
   */
  public <T extends MappedPOIUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  /** @return mappedPOI */
  @JsonIgnore
  public MappedPOI getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return MappedPOIUpdate
   */
  public <T extends MappedPOIUpdate> T setMappedPOI(MappedPOI mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }
}
