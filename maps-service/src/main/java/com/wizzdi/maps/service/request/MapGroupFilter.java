package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MapGroupToMappedPOI;
import java.util.List;
import java.util.Set;

/** Object Used to List MapGroup */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mapGroupMapGroupToMappedPOIs",
      field = "mapGroupMapGroupToMappedPOIsIds",
      fieldType = com.wizzdi.maps.model.MapGroupToMappedPOI.class)
})
public class MapGroupFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> externalId;

  @JsonIgnore private List<MapGroupToMappedPOI> mapGroupMapGroupToMappedPOIs;

  private Set<String> mapGroupMapGroupToMappedPOIsIds;

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return MapGroupFilter
   */
  public <T extends MapGroupFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  /** @return externalId */
  public Set<String> getExternalId() {
    return this.externalId;
  }

  /**
   * @param externalId externalId to set
   * @return MapGroupFilter
   */
  public <T extends MapGroupFilter> T setExternalId(Set<String> externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  /** @return mapGroupMapGroupToMappedPOIs */
  @JsonIgnore
  public List<MapGroupToMappedPOI> getMapGroupMapGroupToMappedPOIs() {
    return this.mapGroupMapGroupToMappedPOIs;
  }

  /**
   * @param mapGroupMapGroupToMappedPOIs mapGroupMapGroupToMappedPOIs to set
   * @return MapGroupFilter
   */
  public <T extends MapGroupFilter> T setMapGroupMapGroupToMappedPOIs(
      List<MapGroupToMappedPOI> mapGroupMapGroupToMappedPOIs) {
    this.mapGroupMapGroupToMappedPOIs = mapGroupMapGroupToMappedPOIs;
    return (T) this;
  }

  /** @return mapGroupMapGroupToMappedPOIsIds */
  public Set<String> getMapGroupMapGroupToMappedPOIsIds() {
    return this.mapGroupMapGroupToMappedPOIsIds;
  }

  /**
   * @param mapGroupMapGroupToMappedPOIsIds mapGroupMapGroupToMappedPOIsIds to set
   * @return MapGroupFilter
   */
  public <T extends MapGroupFilter> T setMapGroupMapGroupToMappedPOIsIds(
      Set<String> mapGroupMapGroupToMappedPOIsIds) {
    this.mapGroupMapGroupToMappedPOIsIds = mapGroupMapGroupToMappedPOIsIds;
    return (T) this;
  }
}
