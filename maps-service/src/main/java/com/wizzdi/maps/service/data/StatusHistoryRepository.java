package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.MapIcon;
import com.wizzdi.maps.model.MapIcon_;
import com.wizzdi.maps.model.MappedPOI;
import com.wizzdi.maps.model.MappedPOI_;
import com.wizzdi.maps.model.StatusHistory;
import com.wizzdi.maps.model.StatusHistory_;
import com.wizzdi.maps.service.request.StatusHistoryFilter;
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
public class StatusHistoryRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;
  @Autowired
  private MappedPOIRepository mappedPOIRepository;

  /**
   * @param statusHistoryFilter Object Used to List StatusHistory
   * @param securityContext
   * @return List of StatusHistory
   */
  public List<StatusHistory> listAllStatusHistories(
      StatusHistoryFilter statusHistoryFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<StatusHistory> q = cb.createQuery(StatusHistory.class);
    Root<StatusHistory> r = q.from(StatusHistory.class);
    List<Predicate> preds = new ArrayList<>();
    addStatusHistoryPredicate(statusHistoryFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0])).orderBy(cb.asc(r.get(StatusHistory_.dateAtStatus)));
    TypedQuery<StatusHistory> query = em.createQuery(q);

    BasicRepository.addPagination(statusHistoryFilter, query);

    return query.getResultList();
  }

  public <T extends StatusHistory> void addStatusHistoryPredicate(
      StatusHistoryFilter statusHistoryFilter,
      CriteriaBuilder cb,
      CriteriaQuery<?> q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        statusHistoryFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (statusHistoryFilter.getMappedPOI() != null
        && !statusHistoryFilter.getMappedPOI().isEmpty()) {
      Set<String> ids =
          statusHistoryFilter.getMappedPOI().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, MappedPOI> join = r.join(StatusHistory_.mappedPOI);
      preds.add(join.get(MappedPOI_.id).in(ids));
    }

    if (statusHistoryFilter.getMapIcon() != null && !statusHistoryFilter.getMapIcon().isEmpty()) {
      Set<String> ids =
          statusHistoryFilter.getMapIcon().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, MapIcon> join = r.join(StatusHistory_.mapIcon);
      preds.add(join.get(MapIcon_.id).in(ids));
    }


    if (statusHistoryFilter.getDateAtStatus() != null
        && !statusHistoryFilter.getDateAtStatus().isEmpty()) {
      preds.add(r.get(StatusHistory_.dateAtStatus).in(statusHistoryFilter.getDateAtStatus()));
    }
    if(statusHistoryFilter.getStartDate()!=null){
      preds.add(cb.greaterThanOrEqualTo(r.get(StatusHistory_.dateAtStatus),statusHistoryFilter.getStartDate()));
    }
    if(statusHistoryFilter.getEndDate()!=null){
      preds.add(cb.lessThanOrEqualTo(r.get(StatusHistory_.dateAtStatus),statusHistoryFilter.getEndDate()));
    }
    if(statusHistoryFilter.getMappedPOIFilter()!=null){
      Join<T, MappedPOI> join = r.join(StatusHistory_.mappedPOI);
      mappedPOIRepository.addMappedPOIPredicate(statusHistoryFilter.getMappedPOIFilter(),cb,q, join,preds,securityContext);
    }
  }
  /**
   * @param statusHistoryFilter Object Used to List StatusHistory
   * @param securityContext
   * @return count of StatusHistory
   */
  public Long countAllStatusHistories(
      StatusHistoryFilter statusHistoryFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<StatusHistory> r = q.from(StatusHistory.class);
    List<Predicate> preds = new ArrayList<>();
    addStatusHistoryPredicate(statusHistoryFilter, cb, q, r, preds, securityContext);
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
