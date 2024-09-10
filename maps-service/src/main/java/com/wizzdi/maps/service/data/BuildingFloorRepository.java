package com.wizzdi.maps.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.maps.model.*;
import com.wizzdi.maps.service.request.BuildingFloorFilter;
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
public class BuildingFloorRepository implements Plugin {
  @PersistenceContext private EntityManager em;

  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param buildingFloorFilter Object Used to List Building
   * @param securityContext
   * @return List of Building
   */
  public List<BuildingFloor> listAllBuildingFloors(
      BuildingFloorFilter buildingFloorFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<BuildingFloor> q = cb.createQuery(BuildingFloor.class);
    Root<BuildingFloor> r = q.from(BuildingFloor.class);
    List<Predicate> preds = new ArrayList<>();
    addBuildingFloorPredicate(buildingFloorFilter, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<BuildingFloor> query = em.createQuery(q);

    BasicRepository.addPagination(buildingFloorFilter, query);

    return query.getResultList();
  }

  public <T extends BuildingFloor> void addBuildingFloorPredicate(
      BuildingFloorFilter buildingFloorFilter,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
            buildingFloorFilter.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    List<Building> buildings = buildingFloorFilter.getBuildings();
    if (buildings != null && !buildings.isEmpty()) {
      preds.add(r.get(BuildingFloor_.building).in(buildings));
    }
    if(buildingFloorFilter.getExternalIds()!=null&&!buildingFloorFilter.getExternalIds().isEmpty()){
      preds.add(r.get(BuildingFloor_.externalId).in(buildingFloorFilter.getExternalIds()));
    }


  }
  /**
   * @param buildingFloorFilter Object Used to List buildingFloor
   * @param securityContext
   * @return count of buildingFloor
   */
  public Long countAllBuildingFloors(
      BuildingFloorFilter buildingFloorFilter, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<BuildingFloor> r = q.from(BuildingFloor.class);
    List<Predicate> preds = new ArrayList<>();
    addBuildingFloorPredicate(buildingFloorFilter, cb, q, r, preds, securityContext);
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

  @Transactional
  public void createBuildingFloorIdx() {
    em.createNativeQuery("""
                CREATE UNIQUE INDEX IF NOT EXISTS building_floor_unique_idx 
                ON BuildingFloor (building_id,externalId) 
                WHERE softdelete = false
                """).executeUpdate();

  }
}
