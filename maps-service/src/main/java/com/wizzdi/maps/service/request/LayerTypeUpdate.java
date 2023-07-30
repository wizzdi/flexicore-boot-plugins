package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.maps.model.Layer;
import com.wizzdi.maps.model.LayerType;

/** Object Used to Update Building */

public class LayerTypeUpdate extends LayerTypeCreate {

  @JsonIgnore private LayerType layerType;

  private String id;


  public LayerType getLayerType() {
    return layerType;
  }


  public LayerTypeUpdate setLayerType(LayerType layerType) {
    this.layerType = layerType;
    return this;
  }

  public String getId() {
    return id;
  }

  public LayerTypeUpdate setId(String id) {
    this.id = id;
    return this;
  }
}
