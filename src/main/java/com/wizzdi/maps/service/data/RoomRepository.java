package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.Building;
import com.wizzdi.maps.model.Building_;
import com.wizzdi.maps.model.LocationHistory;
import com.wizzdi.maps.model.LocationHistory_;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOI_;
import com.wizzdi.maps.model.Room;
import com.wizzdi.maps.model.Room_;
import com.wizzdi.maps.service.request.RoomFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Extension
public class RoomRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @return List of Room
   */
  public List<Room> listAllRooms(RoomFilter roomFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Room> q = cb.createQuery(Room.class);
    Root<Room> r = q.from(Room.class);
    List<Predicate> preds = new ArrayList<>();
    addRoomPredicate(roomFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<Room> query = em.createQuery(q);

    BasicRepository.addPagination(roomFilter, query);

    return query.getResultList();
  }

  public <T extends Room> void addRoomPredicate(
      RoomFilter roomFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        roomFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (roomFilter.getBuilding() != null && !roomFilter.getBuilding().isEmpty()) {
      Set<String> ids =
          roomFilter.getBuilding().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
      Join<T, Building> join = r.join(Room_.building);
      preds.add(join.get(Building_.id).in(ids));
    }

    if (roomFilter.getRoomLocationHistories() != null
        && !roomFilter.getRoomLocationHistories().isEmpty()) {
      Set<String> ids =
          roomFilter.getRoomLocationHistories().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, LocationHistory> join = r.join(Room_.roomLocationHistories);
      preds.add(join.get(LocationHistory_.id).in(ids));
    }

    if (roomFilter.getZ() != null && !roomFilter.getZ().isEmpty()) {
      preds.add(r.get(Room_.z).in(roomFilter.getZ()));
    }

    if (roomFilter.getRoomMappedPOIs() != null && !roomFilter.getRoomMappedPOIs().isEmpty()) {
      Set<String> ids =
          roomFilter.getRoomMappedPOIs().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, MappedPOI> join = r.join(Room_.roomMappedPOIs);
      preds.add(join.get(MappedPOI_.id).in(ids));
    }

    if (roomFilter.getX() != null && !roomFilter.getX().isEmpty()) {
      preds.add(r.get(Room_.x).in(roomFilter.getX()));
    }

    if (roomFilter.getExternalId() != null && !roomFilter.getExternalId().isEmpty()) {
      preds.add(r.get(Room_.externalId).in(roomFilter.getExternalId()));
    }

    if (roomFilter.getY() != null && !roomFilter.getY().isEmpty()) {
      preds.add(r.get(Room_.y).in(roomFilter.getY()));
    }
  }
  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @return count of Room
   */
  public Long countAllRooms(RoomFilter roomFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<Room> r = q.from(Room.class);
    List<Predicate> preds = new ArrayList<>();
    addRoomPredicate(roomFilter, cb, q, r, preds, securityContext);
    q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
    TypedQuery<Long> query = em.createQuery(q);
    return query.getSingleResult();
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return securedBasicRepository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return securedBasicRepository.findByIds(c, ids, idAttribute);
  }

  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return securedBasicRepository.findByIds(c, requested);
  }

  public <T> T findByIdOrNull(Class<T> type, String id) {
    return securedBasicRepository.findByIdOrNull(type, id);
  }

  @Transactional
  public void merge(java.lang.Object base) {
    securedBasicRepository.merge(base);
  }

  @Transactional
  public void massMerge(List<?> toMerge) {
    securedBasicRepository.massMerge(toMerge);
  }
}
