package com.flexicore.rules.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.flexicore.rules.model.DataSource;
import com.flexicore.rules.model.DataSource_;
import com.flexicore.rules.request.DataSourceFilter;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.boot.dynamic.invokers.model.DynamicExecution;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
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

@Extension
@Component
public class DataSourceRepository implements Plugin, IDataSourceRepository {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param filtering Object Used to List DataSource
   * @param securityContext
   * @return List of DataSource
   */
  @Override
  public List<DataSource> listAllDataSources(
      DataSourceFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<DataSource> q = cb.createQuery(DataSource.class);
    Root<DataSource> r = q.from(DataSource.class);
    List<Predicate> preds = new ArrayList<>();
    addDataSourcePredicate(filtering, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<DataSource> query = em.createQuery(q);
    BasicRepository.addPagination(filtering, query);
    return query.getResultList();
  }

  @Override
  public <T extends DataSource> void addDataSourcePredicate(
      DataSourceFilter filtering,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        filtering.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (filtering.getDynamicExecution() != null && !filtering.getDynamicExecution().isEmpty()) {
      Set<String> ids =
          filtering.getDynamicExecution().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, DynamicExecution> join = r.join(DataSource_.dynamicExecution);
      preds.add(join.get(Basic_.id).in(ids));
    }
  }
  /**
   * @param filtering Object Used to List DataSource
   * @param securityContext
   * @return count of DataSource
   */
  @Override
  public Long countAllDataSources(DataSourceFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<DataSource> r = q.from(DataSource.class);
    List<Predicate> preds = new ArrayList<>();
    addDataSourcePredicate(filtering, cb, q, r, preds, securityContext);
    q.select(cb.count(r)).where(preds.toArray(new Predicate[0]));
    TypedQuery<Long> query = em.createQuery(q);
    return query.getSingleResult();
  }

  @Override
  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return securedBasicRepository.listByIds(c, ids, securityContext);
  }

  @Override
  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return securedBasicRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return securedBasicRepository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return securedBasicRepository.findByIds(c, ids, idAttribute);
  }

  @Override
  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return securedBasicRepository.findByIds(c, requested);
  }

  @Override
  public <T> T findByIdOrNull(Class<T> type, String id) {
    return securedBasicRepository.findByIdOrNull(type, id);
  }

  @Override
  @Transactional
  public void merge(java.lang.Object base) {
    securedBasicRepository.merge(base);
  }

  @Override
  @Transactional
  public void massMerge(List<?> toMerge) {
    securedBasicRepository.massMerge(toMerge);
  }
}
