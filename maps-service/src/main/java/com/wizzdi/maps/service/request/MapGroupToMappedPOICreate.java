package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MapGroup;
import com.wizzdi.maps.model.MappedPOI;

/** Object Used to Create MapGroupToMappedPOI */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mapGroup",
      field = "mapGroupId",
      fieldType = com.wizzdi.maps.model.MapGroup.class,
      groups = {
        com.wizzdi.flexicore.security.validation.Create.class,
        com.wizzdi.flexicore.security.validation.Update.class
      }),
  @IdValid(
      targetField = "mappedPOI",
      field = "mappedPOIId",
      fieldType = com.wizzdi.maps.model.MappedPOI.class,
      groups = {
        com.wizzdi.flexicore.security.validation.Create.class,
        com.wizzdi.flexicore.security.validation.Update.class
      })
})
public class MapGroupToMappedPOICreate extends BasicCreate {

  @JsonIgnore private MapGroup mapGroup;

  private String mapGroupId;

  @JsonIgnore private MappedPOI mappedPOI;

  private String mappedPOIId;

  /** @return mapGroup */
  @JsonIgnore
  public MapGroup getMapGroup() {
    return this.mapGroup;
  }

  /**
   * @param mapGroup mapGroup to set
   * @return MapGroupToMappedPOICreate
   */
  public <T extends MapGroupToMappedPOICreate> T setMapGroup(MapGroup mapGroup) {
    this.mapGroup = mapGroup;
    return (T) this;
  }

  /** @return mapGroupId */
  public String getMapGroupId() {
    return this.mapGroupId;
  }

  /**
   * @param mapGroupId mapGroupId to set
   * @return MapGroupToMappedPOICreate
   */
  public <T extends MapGroupToMappedPOICreate> T setMapGroupId(String mapGroupId) {
    this.mapGroupId = mapGroupId;
    return (T) this;
  }

  /** @return mappedPOI */
  @JsonIgnore
  public MappedPOI getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return MapGroupToMappedPOICreate
   */
  public <T extends MapGroupToMappedPOICreate> T setMappedPOI(MappedPOI mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return mappedPOIId */
  public String getMappedPOIId() {
    return this.mappedPOIId;
  }

  /**
   * @param mappedPOIId mappedPOIId to set
   * @return MapGroupToMappedPOICreate
   */
  public <T extends MapGroupToMappedPOICreate> T setMappedPOIId(String mappedPOIId) {
    this.mappedPOIId = mappedPOIId;
    return (T) this;
  }
}
