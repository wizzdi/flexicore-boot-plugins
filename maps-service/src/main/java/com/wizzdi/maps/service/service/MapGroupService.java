package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.MapGroup;
import com.wizzdi.maps.service.data.MapGroupRepository;
import com.wizzdi.maps.service.request.MapGroupCreate;
import com.wizzdi.maps.service.request.MapGroupFilter;
import com.wizzdi.maps.service.request.MapGroupUpdate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class MapGroupService implements Plugin {

  @Autowired private MapGroupRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param mapGroupCreate Object Used to Create MapGroup
   * @param securityContext
   * @return created MapGroup
   */
  public MapGroup createMapGroup(
      MapGroupCreate mapGroupCreate, SecurityContextBase securityContext) {
    MapGroup mapGroup = createMapGroupNoMerge(mapGroupCreate, securityContext);
    this.repository.merge(mapGroup);
    return mapGroup;
  }

  /**
   * @param mapGroupCreate Object Used to Create MapGroup
   * @param securityContext
   * @return created MapGroup unmerged
   */
  public MapGroup createMapGroupNoMerge(
      MapGroupCreate mapGroupCreate, SecurityContextBase securityContext) {
    MapGroup mapGroup = new MapGroup();
    mapGroup.setId(UUID.randomUUID().toString());
    updateMapGroupNoMerge(mapGroup, mapGroupCreate);

    BaseclassService.createSecurityObjectNoMerge(mapGroup, securityContext);

    return mapGroup;
  }

  /**
   * @param mapGroupCreate Object Used to Create MapGroup
   * @param mapGroup
   * @return if mapGroup was updated
   */
  public boolean updateMapGroupNoMerge(MapGroup mapGroup, MapGroupCreate mapGroupCreate) {
    boolean update = basicService.updateBasicNoMerge(mapGroupCreate, mapGroup);

    if (mapGroupCreate.getExternalId() != null
        && (!mapGroupCreate.getExternalId().equals(mapGroup.getExternalId()))) {
      mapGroup.setExternalId(mapGroupCreate.getExternalId());
      update = true;
    }

    return update;
  }
  /**
   * @param mapGroupUpdate
   * @param securityContext
   * @return mapGroup
   */
  public MapGroup updateMapGroup(
      MapGroupUpdate mapGroupUpdate, SecurityContextBase securityContext) {
    MapGroup mapGroup = mapGroupUpdate.getMapGroup();
    if (updateMapGroupNoMerge(mapGroup, mapGroupUpdate)) {
      this.repository.merge(mapGroup);
    }
    return mapGroup;
  }

  /**
   * @param mapGroupFilter Object Used to List MapGroup
   * @param securityContext
   * @return PaginationResponse containing paging information for MapGroup
   */
  public PaginationResponse<MapGroup> getAllMapGroups(
      MapGroupFilter mapGroupFilter, SecurityContextBase securityContext) {
    List<MapGroup> list = listAllMapGroups(mapGroupFilter, securityContext);
    long count = this.repository.countAllMapGroups(mapGroupFilter, securityContext);
    return new PaginationResponse<>(list, mapGroupFilter.getPageSize(), count);
  }

  /**
   * @param mapGroupFilter Object Used to List MapGroup
   * @param securityContext
   * @return List of MapGroup
   */
  public List<MapGroup> listAllMapGroups(
      MapGroupFilter mapGroupFilter, SecurityContextBase securityContext) {
    return this.repository.listAllMapGroups(mapGroupFilter, securityContext);
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

  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
