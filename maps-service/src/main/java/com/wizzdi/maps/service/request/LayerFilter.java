package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.flexicore.annotations.TypeRetention;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.LayerType;
import com.wizzdi.maps.model.MappedPOI;

import java.util.List;
import java.util.Set;

/** Object Used to List Building */
@IdValid.List({
  @IdValid(
      targetField = "layerTypes",
      field = "layerTypeIds",
      fieldType = LayerType.class)
})
public class LayerFilter extends PaginationFilter {

  private BasicPropertiesFilter basicPropertiesFilter;
@TypeRetention(LayerType.class)
  private Set<String> layerTypeIds;
  @JsonIgnore
  private List<LayerType> layerTypes;

  private Set<String> externalIds;


  /** @return basicPropertiesFilter */
  public BasicPropertiesFilter getBasicPropertiesFilter() {
    return this.basicPropertiesFilter;
  }

  /**
   * @param basicPropertiesFilter basicPropertiesFilter to set
   * @return BuildingFilter
   */
  public <T extends LayerFilter> T setBasicPropertiesFilter(
      BasicPropertiesFilter basicPropertiesFilter) {
    this.basicPropertiesFilter = basicPropertiesFilter;
    return (T) this;
  }

  public Set<String> getLayerTypeIds() {
    return layerTypeIds;
  }

  public LayerFilter setLayerTypeIds(Set<String> layerTypeIds) {
    this.layerTypeIds = layerTypeIds;
    return this;
  }

  public List<LayerType> getLayerTypes() {
    return layerTypes;
  }

  public LayerFilter setLayerTypes(List<LayerType> layerTypes) {
    this.layerTypes = layerTypes;
    return this;
  }

  public Set<String> getExternalIds() {
    return externalIds;
  }

  public LayerFilter setExternalIds(Set<String> externalIds) {
    this.externalIds = externalIds;
    return this;
  }
}
