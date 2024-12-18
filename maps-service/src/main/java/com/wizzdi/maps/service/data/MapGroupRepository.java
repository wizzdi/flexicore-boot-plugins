package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.MapGroup;
import com.wizzdi.maps.model.MapGroupToMappedPOI;
import com.wizzdi.maps.model.MapGroupToMappedPOI_;
import com.wizzdi.maps.model.MapGroup_;
import com.wizzdi.maps.service.request.MapGroupFilter;
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
public class MapGroupRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param mapGroupFilter Object Used to List MapGroup
   * @param securityContext
   * @return List of MapGroup
   */
  public List<MapGroup> listAllMapGroups(
      MapGroupFilter mapGroupFilter, SecurityContext securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<MapGroup> q = cb.createQuery(MapGroup.class);
    Root<MapGroup> r = q.from(MapGroup.class);
    List<Predicate> preds = new ArrayList<>();
    addMapGroupPredicate(mapGroupFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<MapGroup> query = em.createQuery(q);

    BasicRepository.addPagination(mapGroupFilter, query);

    return query.getResultList();
  }

  public <T extends MapGroup> void addMapGroupPredicate(
      MapGroupFilter mapGroupFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContext securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        mapGroupFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (mapGroupFilter.getMapGroupMapGroupToMappedPOIs() != null
        && !mapGroupFilter.getMapGroupMapGroupToMappedPOIs().isEmpty()) {
      Set<String> ids =
          mapGroupFilter.getMapGroupMapGroupToMappedPOIs().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, MapGroupToMappedPOI> join = r.join(MapGroup_.mapGroupMapGroupToMappedPOIs);
      preds.add(join.get(MapGroupToMappedPOI_.id).in(ids));
    }

    if (mapGroupFilter.getExternalId() != null && !mapGroupFilter.getExternalId().isEmpty()) {
      preds.add(r.get(MapGroup_.externalId).in(mapGroupFilter.getExternalId()));
    }
  }
  /**
   * @param mapGroupFilter Object Used to List MapGroup
   * @param securityContext
   * @return count of MapGroup
   */
  public Long countAllMapGroups(
      MapGroupFilter mapGroupFilter, SecurityContext securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<MapGroup> r = q.from(MapGroup.class);
    List<Predicate> preds = new ArrayList<>();
    addMapGroupPredicate(mapGroupFilter, cb, q, r, preds, securityContext);
    q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
    TypedQuery<Long> query = em.createQuery(q);
    return query.getSingleResult();
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContext securityContext) {
    return securedBasicRepository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContext securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
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
