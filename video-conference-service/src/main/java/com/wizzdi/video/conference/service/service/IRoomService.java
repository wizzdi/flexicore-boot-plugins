package com.wizzdi.video.conference.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.video.conference.model.Room;
import com.wizzdi.video.conference.service.request.RoomCreate;
import com.wizzdi.video.conference.service.request.RoomFilter;
import com.wizzdi.video.conference.service.request.RoomUpdate;
import java.util.List;
import java.util.Set;
import jakarta.persistence.metamodel.SingularAttribute;

public interface IRoomService {

  /**
   * @param roomCreate Object Used to Create Entity_4
   * @param securityContext
   * @return created Room
   */
  Room createRoom(RoomCreate roomCreate, SecurityContext securityContext);

  /**
   * @param roomCreate Object Used to Create Entity_4
   * @param securityContext
   * @return created Room unmerged
   */
  Room createRoomNoMerge(RoomCreate roomCreate, SecurityContext securityContext);

  /**
   * @param roomCreate Object Used to Create Entity_4
   * @param room
   * @return if room was updated
   */
  boolean updateRoomNoMerge(RoomCreate roomCreate, Room room);

  /**
   * @param roomUpdate
   * @param securityContext
   * @return room
   */
  Room updateRoom(RoomUpdate roomUpdate, SecurityContext securityContext);

  /**
   * @param roomFilter Object Used to List Entity_4
   * @param securityContext
   * @return PaginationResponse containing paging information for Room
   */
  PaginationResponse<Room> getAllRooms(RoomFilter roomFilter, SecurityContext securityContext);

  /**
   * @param roomFilter Object Used to List Entity_4
   * @param securityContext
   * @return List of Room
   */
  List<Room> listAllRooms(RoomFilter roomFilter, SecurityContext securityContext);

  /**
   * @param roomFilter Object Used to List Entity_4
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if roomFilter is not valid
   */
  void validate(RoomFilter roomFilter, SecurityContext securityContext);

  /**
   * @param roomCreate Object Used to Create Entity_4
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if roomCreate is not valid
   */
  void validate(RoomCreate roomCreate, SecurityContext securityContext);

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
