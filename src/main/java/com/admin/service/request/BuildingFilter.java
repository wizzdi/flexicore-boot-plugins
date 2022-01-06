package com.admin.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.maps.model.MappedPOI;
import java.util.List;
import java.util.Set;

public class BuildingFilter extends PaginationFilter {

  private Set<String> mappedPOIIds;

  private Set<String> externalId;

  @JsonIgnore private List<MappedPOI> mappedPOI;

  private BasicPropertiesFilter basicPropertiesFilter;

  public Set<String> getMappedPOIIds() {
    return this.mappedPOIIds;
  }

  public <T extends BuildingFilter> T setMappedPOIIds(Set<String> mappedPOIIds) {
    this.mappedPOIIds = mappedPOIIds;
    return (T) this;
  }

  public Set<String> getExternalId() {
    return this.externalId;
  }

  public <T extends BuildingFilter> T setExternalId(Set<String> externalId) {
    this.externalId = externalId;
    return (T) this;
  }

  @JsonIgnore
  public List<MappedPOI> getMappedPOI() {
    return this.mappedPOI;
  }

  public <T extends BuildingFilter> T setMappedPOI(List<MappedPOI> mappedPOI) {
    this.mappedPOI = mappedPOI;
    return (T) this;
  }

  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  public <T extends BuildingFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }
}
