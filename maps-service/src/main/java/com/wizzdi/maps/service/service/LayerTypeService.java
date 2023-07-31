package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.LayerType;
import com.wizzdi.maps.service.data.LayerTypeRepository;
import com.wizzdi.maps.service.request.LayerTypeCreate;
import com.wizzdi.maps.service.request.LayerTypeFilter;
import com.wizzdi.maps.service.request.LayerTypeUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
@Extension
public class LayerTypeService implements Plugin {

  @Autowired private LayerTypeRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param layerTypeCreate Object Used to Create LayerType
   * @param securityContext
   * @return created LayerType
   */
  public LayerType createLayerType(
      LayerTypeCreate layerTypeCreate, SecurityContextBase securityContext) {
    LayerType layerType = createLayerTypeNoMerge(layerTypeCreate, securityContext);
    this.repository.merge(layerType);
    return layerType;
  }

  /**
   * @param layerTypeCreate Object Used to Create LayerType
   * @param securityContext
   * @return created LayerType unmerged
   */
  public LayerType createLayerTypeNoMerge(
      LayerTypeCreate layerTypeCreate, SecurityContextBase securityContext) {
    LayerType layerType = new LayerType();
    layerType.setId(UUID.randomUUID().toString());
    updateLayerTypeNoMerge(layerType, layerTypeCreate);

    BaseclassService.createSecurityObjectNoMerge(layerType, securityContext);

    return layerType;
  }

  /**
   * @param layerTypeCreate Object Used to Create LayerType
   * @param layerType
   * @return if layerType was updated
   */
  public boolean updateLayerTypeNoMerge(LayerType layerType, LayerTypeCreate layerTypeCreate) {
    boolean update = basicService.updateBasicNoMerge(layerTypeCreate, layerType);


    return update;
  }
  /**
   * @param layerTypeUpdate
   * @param securityContext
   * @return layerType
   */
  public LayerType updateLayerType(
      LayerTypeUpdate layerTypeUpdate, SecurityContextBase securityContext) {
    LayerType layerType = layerTypeUpdate.getLayerType();
    if (updateLayerTypeNoMerge(layerType, layerTypeUpdate)) {
      this.repository.merge(layerType);
    }
    return layerType;
  }

  /**
   * @param layerTypeFilter Object Used to List LayerType
   * @param securityContext
   * @return PaginationResponse containing paging information for LayerType
   */
  public PaginationResponse<LayerType> getAllLayerTypes(
      LayerTypeFilter layerTypeFilter, SecurityContextBase securityContext) {
    List<LayerType> list = listAllLayerTypes(layerTypeFilter, securityContext);
    long count = this.repository.countAllLayerTypes(layerTypeFilter, securityContext);
    return new PaginationResponse<>(list, layerTypeFilter.getPageSize(), count);
  }

  /**
   * @param layerTypeFilter Object Used to List LayerType
   * @param securityContext
   * @return List of LayerType
   */
  public List<LayerType> listAllLayerTypes(
      LayerTypeFilter layerTypeFilter, SecurityContextBase securityContext) {
    return this.repository.listAllLayerTypes(layerTypeFilter, securityContext);
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
