package com.wizzdi.video.conference.service.data;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.video.conference.model.RoomToVideoServerUser;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserFilter;
import java.util.List;
import java.util.Set;
import jakarta.persistence.criteria.*;
import jakarta.persistence.metamodel.SingularAttribute;

public interface IRoomToVideoServerUserRepository {

  /**
   * @param filtering Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @return List of RoomToVideoServerUser
   */
  List<RoomToVideoServerUser> listAllRoomToVideoServerUsers(
      RoomToVideoServerUserFilter filtering, SecurityContext securityContext);

  <T extends RoomToVideoServerUser> void addRoomToVideoServerUserPredicate(
      RoomToVideoServerUserFilter filtering,
      CriteriaBuilder cb,
      CommonAbstractCriteria q,
      From<?, T> r,
      List<Predicate> preds,
      SecurityContext securityContext);

  /**
   * @param filtering Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @return count of RoomToVideoServerUser
   */
  Long countAllRoomToVideoServerUsers(
      RoomToVideoServerUserFilter filtering, SecurityContext securityContext);

  <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContext securityContext);

  <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext);

  <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext);

  <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext);

  <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute);

  <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested);

  <T> T findByIdOrNull(Class<T> type, String id);

  void merge(Object base);

  void massMerge(List<?> toMerge);
}
