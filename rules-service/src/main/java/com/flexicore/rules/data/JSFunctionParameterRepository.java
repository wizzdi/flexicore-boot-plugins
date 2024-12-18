package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.flexicore.rules.model.JSFunction;
import com.flexicore.rules.model.JSFunctionParameter;
import com.flexicore.rules.model.JSFunctionParameter_;
import com.flexicore.rules.request.JSFunctionParameterFilter;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
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

@Extension
@Component
public class JSFunctionParameterRepository implements Plugin {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param jSFunctionParameterFilter Object Used to List JSFunctionParameter
   * @param securityContext
   * @return List of JSFunctionParameter
   */
  public List<JSFunctionParameter> listAllJSFunctionParameters(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContext securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<JSFunctionParameter> q = cb.createQuery(JSFunctionParameter.class);
    Root<JSFunctionParameter> r = q.from(JSFunctionParameter.class);
    List<Predicate> preds = new ArrayList<>();
    addJSFunctionParameterPredicate(jSFunctionParameterFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<JSFunctionParameter> query = em.createQuery(q);
    BasicRepository.addPagination(jSFunctionParameterFilter, query);
    return query.getResultList();
  }

  public <T extends JSFunctionParameter> void addJSFunctionParameterPredicate(
      JSFunctionParameterFilter jSFunctionParameterFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContext securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        jSFunctionParameterFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (jSFunctionParameterFilter.getOrdinal() != null
        && !jSFunctionParameterFilter.getOrdinal().isEmpty()) {
      preds.add(r.get(JSFunctionParameter_.ordinal).in(jSFunctionParameterFilter.getOrdinal()));
    }

    if (jSFunctionParameterFilter.getParameterType() != null
        && !jSFunctionParameterFilter.getParameterType().isEmpty()) {
      preds.add(
          r.get(JSFunctionParameter_.parameterType)
              .in(jSFunctionParameterFilter.getParameterType()));
    }

    if (jSFunctionParameterFilter.getJsFunction() != null
        && !jSFunctionParameterFilter.getJsFunction().isEmpty()) {
      Set<String> ids =
          jSFunctionParameterFilter.getJsFunction().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, JSFunction> join = r.join(JSFunctionParameter_.jsFunction);
      preds.add(join.get(Basic_.id).in(ids));
    }
  }
  /**
   * @param jSFunctionParameterFilter Object Used to List JSFunctionParameter
   * @param securityContext
   * @return count of JSFunctionParameter
   */
  public Long countAllJSFunctionParameters(
      JSFunctionParameterFilter jSFunctionParameterFilter, SecurityContext securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<JSFunctionParameter> r = q.from(JSFunctionParameter.class);
    List<Predicate> preds = new ArrayList<>();
    addJSFunctionParameterPredicate(jSFunctionParameterFilter, cb, q, r, preds, securityContext);
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
