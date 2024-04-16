package com.wizzdi.alerts.data;

import com.wizzdi.alerts.Alert;
import com.wizzdi.alerts.Alert_;
import com.wizzdi.alerts.request.AlertFilter;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Extension
public class AlertRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param alertFilter Object Used to List Alert
   * @param securityContext
   * @return List of Alert
   */
  public List<Alert> listAllAlerts(AlertFilter alertFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Alert> q = cb.createQuery(Alert.class);
    Root<Alert> r = q.from(Alert.class);
    List<Predicate> preds = new ArrayList<>();
    addAlertPredicate(alertFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0])).orderBy(cb.desc(r.get(Alert_.creationDate)));
    TypedQuery<Alert> query = em.createQuery(q);

    BasicRepository.addPagination(alertFilter, query);

    return query.getResultList();
  }

  public <T extends Alert> void addAlertPredicate(
      AlertFilter alertFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        alertFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (alertFilter.getAlertCategory() != null && !alertFilter.getAlertCategory().isEmpty()) {
      preds.add(r.get(Alert_.alertCategory).in(alertFilter.getAlertCategory()));
    }

    if (alertFilter.getAlertLevel() != null && !alertFilter.getAlertLevel().isEmpty()) {
      preds.add(r.get(Alert_.alertLevel).in(alertFilter.getAlertLevel()));
    }

    if (alertFilter.getAlertContent() != null && !alertFilter.getAlertContent().isEmpty()) {
      preds.add(r.get(Alert_.alertContent).in(alertFilter.getAlertContent()));
    }

    if (alertFilter.getRelatedId() != null && !alertFilter.getRelatedId().isEmpty()) {
      preds.add(r.get(Alert_.relatedId).in(alertFilter.getRelatedId()));
    }

    if (alertFilter.getRelatedType() != null && !alertFilter.getRelatedType().isEmpty()) {
      preds.add(r.get(Alert_.relatedType).in(alertFilter.getRelatedType()));
    }
    if(alertFilter.getHandled()!=null){
      preds.add(alertFilter.getHandled()?r.get(Alert_.handledAt).isNotNull():r.get(Alert_.handledAt).isNull());
    }
  }

  /**
   * @param alertFilter Object Used to List Alert
   * @param securityContext
   * @return count of Alert
   */
  public Long countAllAlerts(AlertFilter alertFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<Alert> r = q.from(Alert.class);
    List<Predicate> preds = new ArrayList<>();
    addAlertPredicate(alertFilter, cb, q, r, preds, securityContext);
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
