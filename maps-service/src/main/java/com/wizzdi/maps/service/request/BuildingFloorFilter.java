package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Building;

import java.util.List;
import java.util.Set;


@IdValid.List({
  @IdValid(
      targetField = "buildings",
      field = "buildingIds",
      fieldType = Building.class)
})
public class BuildingFloorFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;
  private Set<String> externalIds;
  @JsonIgnore
  private List<Building> buildings;
  private Set<String> buildingIds;

  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return BuildingFilter
   */
  public <T extends BuildingFloorFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  @JsonIgnore
  public List<Building> getBuildings() {
    return buildings;
  }

  public BuildingFloorFilter setBuildings(List<Building> buildings) {
    this.buildings = buildings;
    return this;
  }

  public Set<String> getBuildingIds() {
    return buildingIds;
  }

  public BuildingFloorFilter setBuildingIds(Set<String> buildingIds) {
    this.buildingIds = buildingIds;
    return this;
  }

  public Set<String> getExternalIds() {
    return externalIds;
  }

  public <T extends BuildingFloorFilter> T setExternalIds(Set<String> externalIds) {
    this.externalIds = externalIds;
    return (T) this;
  }
}
