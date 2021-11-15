package com.wizzdi.video.conference.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.Basic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.data.BasicRepository;
import com.wizzdi.flexicore.security.data.SecuredBasicRepository;
import com.wizzdi.video.conference.model.Room;
import com.wizzdi.video.conference.model.RoomToVideoServerUser;
import com.wizzdi.video.conference.model.RoomToVideoServerUser_;
import com.wizzdi.video.conference.model.VideoServerUser;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserFilter;
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
public class RoomToVideoServerUserRepository implements Plugin, IRoomToVideoServerUserRepository {
  @PersistenceContext private EntityManager em;
  @Autowired private SecuredBasicRepository securedBasicRepository;

  /**
   * @param filtering Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @return List of RoomToVideoServerUser
   */
  @Override
  public List<RoomToVideoServerUser> listAllRoomToVideoServerUsers(
      RoomToVideoServerUserFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<RoomToVideoServerUser> q = cb.createQuery(RoomToVideoServerUser.class);
    Root<RoomToVideoServerUser> r = q.from(RoomToVideoServerUser.class);
    List<Predicate> preds = new ArrayList<>();
    addRoomToVideoServerUserPredicate(filtering, cb, q, r, preds, securityContext);
    q.select(r).where(preds.toArray(new Predicate[0]));
    TypedQuery<RoomToVideoServerUser> query = em.createQuery(q);
    BasicRepository.addPagination(filtering, query);
    return query.getResultList();
  }

  @Override
  public <T extends RoomToVideoServerUser> void addRoomToVideoServerUserPredicate(
      RoomToVideoServerUserFilter filtering,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContextBase securityContext) {

    this.securedBasicRepository.addSecuredBasicPredicates(
        filtering.getBasicPropertiesFilter(), cb, q, r, preds, securityContext);

    if (filtering.getRoom() != null && !filtering.getRoom().isEmpty()) {
      Set<String> ids =
          filtering.getRoom().parallelStream().map(f -> f.getId()).collect(Collectors.toSet());
      Join<T, Room> join = r.join(RoomToVideoServerUser_.room);
      preds.add(join.get(Basic_.id).in(ids));
    }

    if (filtering.getVideoServerUser() != null && !filtering.getVideoServerUser().isEmpty()) {
      Set<String> ids =
          filtering.getVideoServerUser().parallelStream()
              .map(f -> f.getId())
              .collect(Collectors.toSet());
      Join<T, VideoServerUser> join = r.join(RoomToVideoServerUser_.videoServerUser);
      preds.add(join.get(Basic_.id).in(ids));
    }
  }
  /**
   * @param filtering Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @return count of RoomToVideoServerUser
   */
  @Override
  public Long countAllRoomToVideoServerUsers(
      RoomToVideoServerUserFilter filtering, SecurityContextBase securityContext) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Long> q = cb.createQuery(Long.class);
    Root<RoomToVideoServerUser> r = q.from(RoomToVideoServerUser.class);
    List<Predicate> preds = new ArrayList<>();
    addRoomToVideoServerUserPredicate(filtering, cb, q, r, preds, securityContext);
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
  public void merge(Object base) {
    securedBasicRepository.merge(base);
  }

  @Override
  @Transactional
  public void massMerge(List<?> toMerge) {
    securedBasicRepository.massMerge(toMerge);
  }
}
