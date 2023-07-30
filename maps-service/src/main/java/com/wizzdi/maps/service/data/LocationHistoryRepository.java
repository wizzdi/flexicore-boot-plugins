package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.LocationHistory;
import com.wizzdi.maps.model.LocationHistory_;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOI_;
import com.wizzdi.maps.model.Room;
import com.wizzdi.maps.model.Room_;
import com.wizzdi.maps.service.request.LocationHistoryFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Extension
public class LocationHistoryRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param locationHistoryFilter Object Used to List LocationHistory
   * @param securityContext
   * @return List of LocationHistory
   */
  public List<LocationHistory> listAllLocationHistories(
      LocationHistoryFilter locationHistoryFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<LocationHistory> q = cb.createQuery(LocationHistory.class);
    Root<LocationHistory> r = q.from(LocationHistory.class);
    List<Predicate> preds = new ArrayList<>();
    addLocationHistoryPredicate(locationHistoryFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<LocationHistory> query = em.createQuery(q);

    BasicRepository.addPagination(locationHistoryFilter, query);

    return query.getResultList();
  }

  public <T extends LocationHistory> void addLocationHistoryPredicate(
      LocationHistoryFilter locationHistoryFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        locationHistoryFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (locationHistoryFilter.getDateAtLocation() != null
        && !locationHistoryFilter.getDateAtLocation().isEmpty()) {
      preds.add(
          r.get(LocationHistory_.dateAtLocation).in(locationHistoryFilter.getDateAtLocation()));
    }

    if (locationHistoryFilter.getZ() != null && !locationHistoryFilter.getZ().isEmpty()) {
      preds.add(r.get(LocationHistory_.z).in(locationHistoryFilter.getZ()));
    }

    if (locationHistoryFilter.getY() != null && !locationHistoryFilter.getY().isEmpty()) {
      preds.add(r.get(LocationHistory_.y).in(locationHistoryFilter.getY()));
    }

    if (locationHistoryFilter.getRoom() != null && !locationHistoryFilter.getRoom().isEmpty()) {
      Set<String> ids =
          locationHistoryFilter.getRoom().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, Room> join = r.join(LocationHistory_.room);
      preds.add(join.get(Room_.id).in(ids));
    }

    if (locationHistoryFilter.getMappedPOI() != null
        && !locationHistoryFilter.getMappedPOI().isEmpty()) {
      Set<String> ids =
          locationHistoryFilter.getMappedPOI().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, MappedPOI> join = r.join(LocationHistory_.mappedPOI);
      preds.add(join.get(MappedPOI_.id).in(ids));
    }

    if (locationHistoryFilter.getX() != null && !locationHistoryFilter.getX().isEmpty()) {
      preds.add(r.get(LocationHistory_.x).in(locationHistoryFilter.getX()));
    }

    if (locationHistoryFilter.getLon() != null && !locationHistoryFilter.getLon().isEmpty()) {
      preds.add(r.get(LocationHistory_.lon).in(locationHistoryFilter.getLon()));
    }

    if (locationHistoryFilter.getLat() != null && !locationHistoryFilter.getLat().isEmpty()) {
      preds.add(r.get(LocationHistory_.lat).in(locationHistoryFilter.getLat()));
    }
  }
  /**
   * @param locationHistoryFilter Object Used to List LocationHistory
   * @param securityContext
   * @return count of LocationHistory
   */
  public Long countAllLocationHistories(
      LocationHistoryFilter locationHistoryFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<LocationHistory> r = q.from(LocationHistory.class);
    List<Predicate> preds = new ArrayList<>();
    addLocationHistoryPredicate(locationHistoryFilter, cb, q, r, preds, securityContext);
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
