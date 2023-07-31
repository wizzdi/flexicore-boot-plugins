package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.service.data.MapIconRepository;
import com.wizzdi.maps.service.request.MapIconCreate;
import com.wizzdi.maps.service.request.MapIconFilter;
import com.wizzdi.maps.service.request.MapIconUpdate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class MapIconService implements Plugin {

  @Autowired private MapIconRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param mapIconCreate Object Used to Create MapIcon
   * @param securityContext
   * @return created MapIcon
   */
  public MapIcon createMapIcon(MapIconCreate mapIconCreate, SecurityContextBase securityContext) {
    MapIcon mapIcon = createMapIconNoMerge(mapIconCreate, securityContext);
    this.repository.merge(mapIcon);
    return mapIcon;
  }

  /**
   * @param mapIconCreate Object Used to Create MapIcon
   * @param securityContext
   * @return created MapIcon unmerged
   */
  public MapIcon createMapIconNoMerge(
      MapIconCreate mapIconCreate, SecurityContextBase securityContext) {
    MapIcon mapIcon = new MapIcon();
    mapIcon.setId(UUID.randomUUID().toString());
    updateMapIconNoMerge(mapIcon, mapIconCreate);

    BaseclassService.createSecurityObjectNoMerge(mapIcon, securityContext);

    return mapIcon;
  }

  /**
   * @param mapIconCreate Object Used to Create MapIcon
   * @param mapIcon
   * @return if mapIcon was updated
   */
  public boolean updateMapIconNoMerge(MapIcon mapIcon, MapIconCreate mapIconCreate) {
    boolean update = basicService.updateBasicNoMerge(mapIconCreate, mapIcon);

    if (mapIconCreate.getFileResource() != null
        && (mapIcon.getFileResource() == null
            || !mapIconCreate
                .getFileResource()
                .getId()
                .equals(mapIcon.getFileResource().getId()))) {
      mapIcon.setFileResource(mapIconCreate.getFileResource());
      update = true;
    }

    if (mapIconCreate.getRelatedType() != null
        && (!mapIconCreate.getRelatedType().equals(mapIcon.getRelatedType()))) {
      mapIcon.setRelatedType(mapIconCreate.getRelatedType());
      update = true;
    }

    if (mapIconCreate.getExternalId() != null
        && (!mapIconCreate.getExternalId().equals(mapIcon.getExternalId()))) {
      mapIcon.setExternalId(mapIconCreate.getExternalId());
      update = true;
    }

    return update;
  }
  /**
   * @param mapIconUpdate
   * @param securityContext
   * @return mapIcon
   */
  public MapIcon updateMapIcon(MapIconUpdate mapIconUpdate, SecurityContextBase securityContext) {
    MapIcon mapIcon = mapIconUpdate.getMapIcon();
    if (updateMapIconNoMerge(mapIcon, mapIconUpdate)) {
      this.repository.merge(mapIcon);
    }
    return mapIcon;
  }

  /**
   * @param mapIconFilter Object Used to List MapIcon
   * @param securityContext
   * @return PaginationResponse containing paging information for MapIcon
   */
  public PaginationResponse<MapIcon> getAllMapIcons(
      MapIconFilter mapIconFilter, SecurityContextBase securityContext) {
    List<MapIcon> list = listAllMapIcons(mapIconFilter, securityContext);
    long count = this.repository.countAllMapIcons(mapIconFilter, securityContext);
    return new PaginationResponse<>(list, mapIconFilter.getPageSize(), count);
  }

  /**
   * @param mapIconFilter Object Used to List MapIcon
   * @param securityContext
   * @return List of MapIcon
   */
  public List<MapIcon> listAllMapIcons(
      MapIconFilter mapIconFilter, SecurityContextBase securityContext) {
    return this.repository.listAllMapIcons(mapIconFilter, securityContext);
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
