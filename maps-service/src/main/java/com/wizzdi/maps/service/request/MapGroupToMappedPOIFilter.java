package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MapGroup;
import com.wizzdi.maps.model.MappedPOI;
import java.util.List;
import java.util.Set;

/** Object Used to List MapGroupToMappedPOI */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mapGroup",
      field = "mapGroupIds",
      fieldType = com.wizzdi.maps.model.MapGroup.class),
  @IdValid(
      targetField = "mappedPOI",
      field = "mappedPOIIds",
      fieldType = com.wizzdi.maps.model.MappedPOI.class)
})
public class MapGroupToMappedPOIFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;

  @JsonIgnore private List<MapGroup> mapGroup;

  private Set<String> mapGroupIds;

  @JsonIgnore private List<MappedPOI> mappedPOI;

  private Set<String> mappedPOIIds;

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return MapGroupToMappedPOIFilter
   */
  public <T extends MapGroupToMappedPOIFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return mapGroup */
  @JsonIgnore
  public List<MapGroup> getMapGroup() {
    return this.mapGroup;
  }

  /**
   * @param mapGroup mapGroup to set
   * @return MapGroupToMappedPOIFilter
   */
  public <T extends MapGroupToMappedPOIFilter> T setMapGroup(List<MapGroup> mapGroup) {
    this.mapGroup = mapGroup;
    return (T) this;
  }

  /** @return mapGroupIds */
  public Set<String> getMapGroupIds() {
    return this.mapGroupIds;
  }

  /**
   * @param mapGroupIds mapGroupIds to set
   * @return MapGroupToMappedPOIFilter
   */
  public <T extends MapGroupToMappedPOIFilter> T setMapGroupIds(Set<String> mapGroupIds) {
    this.mapGroupIds = mapGroupIds;
    return (T) this;
  }

  /** @return mappedPOI */
  @JsonIgnore
  public List<MappedPOI> getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return MapGroupToMappedPOIFilter
   */
  public <T extends MapGroupToMappedPOIFilter> T setMappedPOI(List<MappedPOI> mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return mappedPOIIds */
  public Set<String> getMappedPOIIds() {
    return this.mappedPOIIds;
  }

  /**
   * @param mappedPOIIds mappedPOIIds to set
   * @return MapGroupToMappedPOIFilter
   */
  public <T extends MapGroupToMappedPOIFilter> T setMappedPOIIds(Set<String> mappedPOIIds) {
    this.mappedPOIIds = mappedPOIIds;
    return (T) this;
  }
}
