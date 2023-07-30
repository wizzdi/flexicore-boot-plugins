package com.wizzdi.maps.service.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.validation.IdValid;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.Layer;

/** Object Used to Update Building */

public class LayerUpdate extends LayerCreate {

  @JsonIgnore private Layer layer;

  private String id;


  /** @return id */
  public String getId() {
    return this.id;
  }

  /**
   * @param id id to set
   * @return BuildingUpdate
   */
  public <T extends LayerUpdate> T setId(String id) {
    this.id = id;
    return (T) this;
  }

  public Layer getLayer() {
    return layer;
  }

  public LayerUpdate setLayer(Layer layer) {
    this.layer = layer;
    return this;
  }
}
