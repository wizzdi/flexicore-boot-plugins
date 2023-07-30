package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.MappedPOI;
import java.util.List;
import java.util.Set;

/** Object Used to List Building */
@com.wizzdi.flexicore.security.validation.IdValid.List({
  @IdValid(
      targetField = "mappedPOI",
      field = "mappedPOIIds",
      fieldType = com.wizzdi.maps.model.MappedPOI.class)
})
public class BuildingFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;

  private Set<String> externalId;

  @JsonIgnore private List<MappedPOI> mappedPOI;

  private Set<String> mappedPOIIds;

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return BuildingFilter
   */
  public <T extends BuildingFilter> T setBasicPropertiesFilter(
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
   * @return BuildingFilter
   */
  public <T extends BuildingFilter> T setExternalId(Set<String> externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  /** @return mappedPOI */
  @JsonIgnore
  public List<MappedPOI> getMappedPOI() {
    return this.mappedPOI;
  }

  /**
   * @param mappedPOI mappedPOI to set
   * @return BuildingFilter
   */
  public <T extends BuildingFilter> T setMappedPOI(List<MappedPOI> mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  /** @return mappedPOIIds */
  public Set<String> getMappedPOIIds() {
    return this.mappedPOIIds;
  }

  /**
   * @param mappedPOIIds mappedPOIIds to set
   * @return BuildingFilter
   */
  public <T extends BuildingFilter> T setMappedPOIIds(Set<String> mappedPOIIds) {
    this.mappedPOIIds = mappedPOIIds;
    return (T) this;
  }
}
