package com.wizzdi.video.conference.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.video.conference.model.RoomToVideoServerUser;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserCreate;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserFilter;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserUpdate;
import java.util.List;
import java.util.Set;
import jakarta.persistence.metamodel.SingularAttribute;

public interface IRoomToVideoServerUserService {

  /**
   * @param roomToVideoServerUserCreate Object Used to Create RoomToVideoServerUser
   * @param securityContext
   * @return created RoomToVideoServerUser
   */
  RoomToVideoServerUser createRoomToVideoServerUser(
      RoomToVideoServerUserCreate roomToVideoServerUserCreate, SecurityContext securityContext);

  /**
   * @param roomToVideoServerUserCreate Object Used to Create RoomToVideoServerUser
   * @param securityContext
   * @return created RoomToVideoServerUser unmerged
   */
  RoomToVideoServerUser createRoomToVideoServerUserNoMerge(
      RoomToVideoServerUserCreate roomToVideoServerUserCreate, SecurityContext securityContext);

  /**
   * @param roomToVideoServerUserCreate Object Used to Create RoomToVideoServerUser
   * @param roomToVideoServerUser
   * @return if roomToVideoServerUser was updated
   */
  boolean updateRoomToVideoServerUserNoMerge(
      RoomToVideoServerUserCreate roomToVideoServerUserCreate,
      RoomToVideoServerUser roomToVideoServerUser);

  /**
   * @param roomToVideoServerUserUpdate
   * @param securityContext
   * @return roomToVideoServerUser
   */
  RoomToVideoServerUser updateRoomToVideoServerUser(
      RoomToVideoServerUserUpdate roomToVideoServerUserUpdate, SecurityContext securityContext);

  /**
   * @param roomToVideoServerUserFilter Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @return PaginationResponse containing paging information for RoomToVideoServerUser
   */
  PaginationResponse<RoomToVideoServerUser> getAllRoomToVideoServerUsers(
      RoomToVideoServerUserFilter roomToVideoServerUserFilter, SecurityContext securityContext);

  /**
   * @param roomToVideoServerUserFilter Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @return List of RoomToVideoServerUser
   */
  List<RoomToVideoServerUser> listAllRoomToVideoServerUsers(
      RoomToVideoServerUserFilter roomToVideoServerUserFilter, SecurityContext securityContext);

  /**
   * @param roomToVideoServerUserFilter Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if roomToVideoServerUserFilter is not valid
   */
  void validate(
      RoomToVideoServerUserFilter roomToVideoServerUserFilter, SecurityContext securityContext);

  /**
   * @param roomToVideoServerUserCreate Object Used to Create RoomToVideoServerUser
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if roomToVideoServerUserCreate is not valid
   */
  void validate(
      RoomToVideoServerUserCreate roomToVideoServerUserCreate, SecurityContext securityContext);

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
