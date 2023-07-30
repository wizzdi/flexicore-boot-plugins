package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.MapGroup;
import com.wizzdi.maps.model.MapGroupToMappedPOI;
import com.wizzdi.maps.model.MapGroupToMappedPOI_;
import com.wizzdi.maps.model.MapGroup_;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOI_;
import com.wizzdi.maps.service.request.MapGroupToMappedPOIFilter;
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
public class MapGroupToMappedPOIRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param mapGroupToMappedPOIFilter Object Used to List MapGroupToMappedPOI
   * @param securityContext
   * @return List of MapGroupToMappedPOI
   */
  public List<MapGroupToMappedPOI> listAllMapGroupToMappedPOIs(
      MapGroupToMappedPOIFilter mapGroupToMappedPOIFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<MapGroupToMappedPOI> q = cb.createQuery(MapGroupToMappedPOI.class);
    Root<MapGroupToMappedPOI> r = q.from(MapGroupToMappedPOI.class);
    List<Predicate> preds = new ArrayList<>();
    addMapGroupToMappedPOIPredicate(mapGroupToMappedPOIFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<MapGroupToMappedPOI> query = em.createQuery(q);

    BasicRepository.addPagination(mapGroupToMappedPOIFilter, query);

    return query.getResultList();
  }

  public <T extends MapGroupToMappedPOI> void addMapGroupToMappedPOIPredicate(
      MapGroupToMappedPOIFilter mapGroupToMappedPOIFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        mapGroupToMappedPOIFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (mapGroupToMappedPOIFilter.getMappedPOI() != null
        && !mapGroupToMappedPOIFilter.getMappedPOI().isEmpty()) {
      Set<String> ids =
          mapGroupToMappedPOIFilter.getMappedPOI().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, MappedPOI> join = r.join(MapGroupToMappedPOI_.mappedPOI);
      preds.add(join.get(MappedPOI_.id).in(ids));
    }

    if (mapGroupToMappedPOIFilter.getMapGroup() != null
        && !mapGroupToMappedPOIFilter.getMapGroup().isEmpty()) {
      Set<String> ids =
          mapGroupToMappedPOIFilter.getMapGroup().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, MapGroup> join = r.join(MapGroupToMappedPOI_.mapGroup);
      preds.add(join.get(MapGroup_.id).in(ids));
    }
  }
  /**
   * @param mapGroupToMappedPOIFilter Object Used to List MapGroupToMappedPOI
   * @param securityContext
   * @return count of MapGroupToMappedPOI
   */
  public Long countAllMapGroupToMappedPOIs(
      MapGroupToMappedPOIFilter mapGroupToMappedPOIFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<MapGroupToMappedPOI> r = q.from(MapGroupToMappedPOI.class);
    List<Predicate> preds = new ArrayList<>();
    addMapGroupToMappedPOIPredicate(mapGroupToMappedPOIFilter, cb, q, r, preds, securityContext);
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
