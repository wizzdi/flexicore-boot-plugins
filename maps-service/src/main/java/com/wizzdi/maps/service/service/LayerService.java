package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.Layer;
import com.wizzdi.maps.service.data.LayerRepository;
import com.wizzdi.maps.service.request.LayerCreate;
import com.wizzdi.maps.service.request.LayerFilter;
import com.wizzdi.maps.service.request.LayerUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Extension
public class LayerService implements Plugin {

  @Autowired private LayerRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param layerCreate Object Used to Create Layer
   * @param securityContext
   * @return created Layer
   */
  public Layer createLayer(
      LayerCreate layerCreate, SecurityContextBase securityContext) {
    Layer layer = createLayerNoMerge(layerCreate, securityContext);
    this.repository.merge(layer);
    return layer;
  }

  /**
   * @param layerCreate Object Used to Create Layer
   * @param securityContext
   * @return created Layer unmerged
   */
  public Layer createLayerNoMerge(
      LayerCreate layerCreate, SecurityContextBase securityContext) {
    Layer layer = new Layer();
    layer.setId(UUID.randomUUID().toString());
    updateLayerNoMerge(layer, layerCreate);

    BaseclassService.createSecurityObjectNoMerge(layer, securityContext);

    return layer;
  }

  /**
   * @param layerCreate Object Used to Create Layer
   * @param layer
   * @return if layer was updated
   */
  public boolean updateLayerNoMerge(Layer layer, LayerCreate layerCreate) {
    boolean update = basicService.updateBasicNoMerge(layerCreate, layer);

    if (layerCreate.getLayerType() != null
        && (layer.getLayerType() == null
            || !layerCreate.getLayerType().getId().equals(layer.getLayerType().getId()))) {
      layer.setLayerType(layerCreate.getLayerType());
      update = true;
    }
    if (layerCreate.getExternalId() != null
            && (layer.getExternalId() == null
            || !layerCreate.getExternalId().equals(layer.getExternalId()))) {
      layer.setExternalId(layerCreate.getExternalId());
      update = true;
    }



    return update;
  }
  /**
   * @param layerUpdate
   * @param securityContext
   * @return layer
   */
  public Layer updateLayer(
      LayerUpdate layerUpdate, SecurityContextBase securityContext) {
    Layer layer = layerUpdate.getLayer();
    if (updateLayerNoMerge(layer, layerUpdate)) {
      this.repository.merge(layer);
    }
    return layer;
  }

  /**
   * @param layerFilter Object Used to List Layer
   * @param securityContext
   * @return PaginationResponse containing paging information for Layer
   */
  public PaginationResponse<Layer> getAllLayers(
      LayerFilter layerFilter, SecurityContextBase securityContext) {
    List<Layer> list = listAllLayers(layerFilter, securityContext);
    long count = this.repository.countAllLayers(layerFilter, securityContext);
    return new PaginationResponse<>(list, layerFilter.getPageSize(), count);
  }

  /**
   * @param layerFilter Object Used to List Layer
   * @param securityContext
   * @return List of Layer
   */
  public List<Layer> listAllLayers(
      LayerFilter layerFilter, SecurityContextBase securityContext) {
    return this.repository.listAllLayers(layerFilter, securityContext);
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return this.repository.findByIds(c, ids, idAttribute);
  }

  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return this.repository.findByIds(c, requested);
  }

  public <T> T findByIdOrNull(Class<T> type, String id) {
    return this.repository.findByIdOrNull(type, id);
  }

  public void merge(Object base) {
    this.repository.merge(base);
  }

  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
