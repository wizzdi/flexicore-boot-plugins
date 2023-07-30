package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.LocationHistory;
import com.wizzdi.maps.service.data.LocationHistoryRepository;
import com.wizzdi.maps.service.request.LocationHistoryCreate;
import com.wizzdi.maps.service.request.LocationHistoryFilter;
import com.wizzdi.maps.service.request.LocationHistoryUpdate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class LocationHistoryService implements Plugin {

  @Autowired private LocationHistoryRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param locationHistoryCreate Object Used to Create LocationHistory
   * @param securityContext
   * @return created LocationHistory
   */
  public LocationHistory createLocationHistory(
      LocationHistoryCreate locationHistoryCreate, SecurityContextBase securityContext) {
    LocationHistory locationHistory =
        createLocationHistoryNoMerge(locationHistoryCreate, securityContext);
    this.repository.merge(locationHistory);
    return locationHistory;
  }

  /**
   * @param locationHistoryCreate Object Used to Create LocationHistory
   * @param securityContext
   * @return created LocationHistory unmerged
   */
  public LocationHistory createLocationHistoryNoMerge(
      LocationHistoryCreate locationHistoryCreate, SecurityContextBase securityContext) {
    LocationHistory locationHistory = new LocationHistory();
    locationHistory.setId(UUID.randomUUID().toString());
    updateLocationHistoryNoMerge(locationHistory, locationHistoryCreate);

    BaseclassService.createSecurityObjectNoMerge(locationHistory, securityContext);

    return locationHistory;
  }

  /**
   * @param locationHistoryCreate Object Used to Create LocationHistory
   * @param locationHistory
   * @return if locationHistory was updated
   */
  public boolean updateLocationHistoryNoMerge(
      LocationHistory locationHistory, LocationHistoryCreate locationHistoryCreate) {
    boolean update = basicService.updateBasicNoMerge(locationHistoryCreate, locationHistory);

    if (locationHistoryCreate.getDateAtLocation() != null
        && (!locationHistoryCreate
            .getDateAtLocation()
            .equals(locationHistory.getDateAtLocation()))) {
      locationHistory.setDateAtLocation(locationHistoryCreate.getDateAtLocation());
      update = true;
    }

    if (locationHistoryCreate.getZ() != null
        && (!locationHistoryCreate.getZ().equals(locationHistory.getZ()))) {
      locationHistory.setZ(locationHistoryCreate.getZ());
      update = true;
    }

    if (locationHistoryCreate.getY() != null
        && (!locationHistoryCreate.getY().equals(locationHistory.getY()))) {
      locationHistory.setY(locationHistoryCreate.getY());
      update = true;
    }

    if (locationHistoryCreate.getRoom() != null
        && (locationHistory.getRoom() == null
            || !locationHistoryCreate
                .getRoom()
                .getId()
                .equals(locationHistory.getRoom().getId()))) {
      locationHistory.setRoom(locationHistoryCreate.getRoom());
      update = true;
    }

    if (locationHistoryCreate.getMappedPOI() != null
        && (locationHistory.getMappedPOI() == null
            || !locationHistoryCreate
                .getMappedPOI()
                .getId()
                .equals(locationHistory.getMappedPOI().getId()))) {
      locationHistory.setMappedPOI(locationHistoryCreate.getMappedPOI());
      update = true;
    }

    if (locationHistoryCreate.getX() != null
        && (!locationHistoryCreate.getX().equals(locationHistory.getX()))) {
      locationHistory.setX(locationHistoryCreate.getX());
      update = true;
    }

    if (locationHistoryCreate.getLon() != null
        && (!locationHistoryCreate.getLon().equals(locationHistory.getLon()))) {
      locationHistory.setLon(locationHistoryCreate.getLon());
      update = true;
    }

    if (locationHistoryCreate.getLat() != null
        && (!locationHistoryCreate.getLat().equals(locationHistory.getLat()))) {
      locationHistory.setLat(locationHistoryCreate.getLat());
      update = true;
    }

    return update;
  }
  /**
   * @param locationHistoryUpdate
   * @param securityContext
   * @return locationHistory
   */
  public LocationHistory updateLocationHistory(
      LocationHistoryUpdate locationHistoryUpdate, SecurityContextBase securityContext) {
    LocationHistory locationHistory = locationHistoryUpdate.getLocationHistory();
    if (updateLocationHistoryNoMerge(locationHistory, locationHistoryUpdate)) {
      this.repository.merge(locationHistory);
    }
    return locationHistory;
  }

  /**
   * @param locationHistoryFilter Object Used to List LocationHistory
   * @param securityContext
   * @return PaginationResponse<LocationHistory> containing paging information for LocationHistory
   */
  public PaginationResponse<LocationHistory> getAllLocationHistories(
      LocationHistoryFilter locationHistoryFilter, SecurityContextBase securityContext) {
    List<LocationHistory> list = listAllLocationHistories(locationHistoryFilter, securityContext);
    long count = this.repository.countAllLocationHistories(locationHistoryFilter, securityContext);
    return new PaginationResponse<>(list, locationHistoryFilter.getPageSize(), count);
  }

  /**
   * @param locationHistoryFilter Object Used to List LocationHistory
   * @param securityContext
   * @return List of LocationHistory
   */
  public List<LocationHistory> listAllLocationHistories(
      LocationHistoryFilter locationHistoryFilter, SecurityContextBase securityContext) {
    return this.repository.listAllLocationHistories(locationHistoryFilter, securityContext);
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
