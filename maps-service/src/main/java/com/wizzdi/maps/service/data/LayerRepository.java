package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.*;
import com.wizzdi.maps.service.request.LayerFilter;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Extension
public class LayerRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;


  public List<Layer> listAllLayers(
          LayerFilter layerFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Layer> q = cb.createQuery(Layer.class);
    Root<Layer> r = q.from(Layer.class);
    List<Predicate> preds = new ArrayList<>();
    addLayerPredicate(layerFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<Layer> query = em.createQuery(q);

    BasicRepository.addPagination(layerFilter, query);

    return query.getResultList();
  }

  public <T extends Layer> void addLayerPredicate(
      LayerFilter layerFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        layerFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (layerFilter.getLayerTypes() != null && !layerFilter.getLayerTypes().isEmpty()) {
      Set<String> ids =
          layerFilter.getLayerTypes().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, LayerType> join = r.join(Layer_.layerType);
      preds.add(join.get(LayerType_.id).in(ids));
    }
    if (layerFilter.getExternalIds() != null && !layerFilter.getExternalIds().isEmpty()) {

      preds.add(r.get(Layer_.externalId).in(layerFilter.getExternalIds()));
    }


  }
  /**
   * @param layerFilter Object Used to List Layer
   * @param securityContext
   * @return count of Layer
   */
  public Long countAllLayers(
      LayerFilter layerFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<Layer> r = q.from(Layer.class);
    List<Predicate> preds = new ArrayList<>();
    addLayerPredicate(layerFilter, cb, q, r, preds, securityContext);
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
  public void merge(Object base) {
    securedBasicRepository.merge(base);
  }

  @Transactional
  public void massMerge(List<?> toMerge) {
    securedBasicRepository.massMerge(toMerge);
  }
}
