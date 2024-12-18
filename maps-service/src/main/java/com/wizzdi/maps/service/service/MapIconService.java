package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.events.BasicUpdated;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.service.data.MapIconRepository;
import com.wizzdi.maps.service.request.MapIconCreate;
import com.wizzdi.maps.service.request.MapIconFilter;
import com.wizzdi.maps.service.request.MapIconUpdate;

import java.util.*;

import io.micrometer.core.instrument.Tag;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.metrics.cache.CacheMetricsRegistrar;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Extension
public class MapIconService implements Plugin {

  private static final String CACHE_NAME = "mapIconCache";
  @Autowired private MapIconRepository repository;

  @Autowired private BasicService basicService;

  @Autowired
  @Qualifier("mapIconCacheManager")
  private CacheManager mapIconCacheManager;


  public MapIconService(@Qualifier("mapIconCacheManager") CacheManager mapIconCacheManager, CacheMetricsRegistrar cacheMetricsRegistrar) {
    cacheMetricsRegistrar.bindCacheToRegistry(mapIconCacheManager.getCache(CACHE_NAME), Tag.of("cache.manager", "mapIconCacheManager"));

  }

  /**
   * @param mapIconCreate Object Used to Create MapIcon
   * @param securityContext
   * @return created MapIcon
   */
  public MapIcon createMapIcon(MapIconCreate mapIconCreate, SecurityContext securityContext) {
    MapIcon mapIcon = createMapIconNoMerge(mapIconCreate, securityContext);
    this.repository.merge(mapIcon);
    return mapIcon;
  }


  @Cacheable(cacheNames = CACHE_NAME, key = "#mapIconCreate.externalId+'|'+#mapIconCreate.relatedType", cacheManager = "mapIconCacheManager")
  public MapIcon getOrCreateMapIcon(MapIconCreate mapIconCreate, SecurityContext SecurityContext) {
    String externalId = mapIconCreate.getExternalId();
    String relatedType = mapIconCreate.getRelatedType();
    return listAllMapIcons(new MapIconFilter().setRelatedType(Collections.singleton(relatedType))
            .setExternalId(Collections.singleton(externalId)), null).stream().filter(f -> f.getTenant().getId().equals(SecurityContext.getTenantToCreateIn().getId()))
            .findFirst().orElseGet(() -> createMapIcon(mapIconCreate, SecurityContext));
  }

  @EventListener
  public void onMapIconUpdated(BasicUpdated<MapIcon> mapIconBasicUpdated) {
    MapIcon mapIcon = mapIconBasicUpdated.getBaseclass();
    Optional.ofNullable(mapIconCacheManager.getCache(CACHE_NAME)).ifPresent(cache->cache.evict(getMapIconCacheKey(mapIcon.getExternalId(), mapIcon.getRelatedType())));
  }

  public String getMapIconCacheKey(String externalId, String relatedType) {
    return externalId + "|" + relatedType;
  }

  /**
   * @param mapIconCreate Object Used to Create MapIcon
   * @param securityContext
   * @return created MapIcon unmerged
   */
  public MapIcon createMapIconNoMerge(
      MapIconCreate mapIconCreate, SecurityContext securityContext) {
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
  public MapIcon updateMapIcon(MapIconUpdate mapIconUpdate, SecurityContext securityContext) {
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
      MapIconFilter mapIconFilter, SecurityContext securityContext) {
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
      MapIconFilter mapIconFilter, SecurityContext securityContext) {
    return this.repository.listAllMapIcons(mapIconFilter, securityContext);
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContext securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContext securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
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
