package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.file.model.FileResource_;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MapIcon_;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOI_;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.model.StatusHistory_;
import com.wizzdi.maps.service.request.MapIconFilter;
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
public class MapIconRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param mapIconFilter Object Used to List MapIcon
   * @param securityContext
   * @return List of MapIcon
   */
  public List<MapIcon> listAllMapIcons(
      MapIconFilter mapIconFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<MapIcon> q = cb.createQuery(MapIcon.class);
    Root<MapIcon> r = q.from(MapIcon.class);
    List<Predicate> preds = new ArrayList<>();
    addMapIconPredicate(mapIconFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0])).orderBy(cb.asc(r.get(MapIcon_.name)));
    TypedQuery<MapIcon> query = em.createQuery(q);

    BasicRepository.addPagination(mapIconFilter, query);

    return query.getResultList();
  }

  public <T extends MapIcon> void addMapIconPredicate(
      MapIconFilter mapIconFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        mapIconFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (mapIconFilter.getFileResource() != null && !mapIconFilter.getFileResource().isEmpty()) {
      Set<String> ids =
          mapIconFilter.getFileResource().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, FileResource> join = r.join(MapIcon_.fileResource);
      preds.add(join.get(FileResource_.id).in(ids));
    }

    if (mapIconFilter.getRelatedType() != null && !mapIconFilter.getRelatedType().isEmpty()) {
      preds.add(r.get(MapIcon_.relatedType).in(mapIconFilter.getRelatedType()));
    }

    if (mapIconFilter.getMapIconMappedPOIs() != null
        && !mapIconFilter.getMapIconMappedPOIs().isEmpty()) {
      Set<String> ids =
          mapIconFilter.getMapIconMappedPOIs().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, MappedPOI> join = r.join(MapIcon_.mapIconMappedPOIs);
      preds.add(join.get(MappedPOI_.id).in(ids));
    }

    if (mapIconFilter.getMapIconStatusHistories() != null
        && !mapIconFilter.getMapIconStatusHistories().isEmpty()) {
      Set<String> ids =
          mapIconFilter.getMapIconStatusHistories().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, StatusHistory> join = r.join(MapIcon_.mapIconStatusHistories);
      preds.add(join.get(StatusHistory_.id).in(ids));
    }

    if (mapIconFilter.getExternalId() != null && !mapIconFilter.getExternalId().isEmpty()) {
      Predicate in = r.get(MapIcon_.externalId).in(mapIconFilter.getExternalId());
      preds.add(mapIconFilter.isExternalIdExclude()?cb.not(in):in);
    }
  }
  /**
   * @param mapIconFilter Object Used to List MapIcon
   * @param securityContext
   * @return count of MapIcon
   */
  public Long countAllMapIcons(MapIconFilter mapIconFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<MapIcon> r = q.from(MapIcon.class);
    List<Predicate> preds = new ArrayList<>();
    addMapIconPredicate(mapIconFilter, cb, q, r, preds, securityContext);
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
