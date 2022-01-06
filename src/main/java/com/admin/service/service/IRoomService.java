package com.admin.service.service;

import com.admin.model.Room;
import com.admin.service.request.RoomCreate;
import com.admin.service.request.RoomFilter;
import com.admin.service.request.RoomUpdate;
import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IRoomService {

  /**
   * @param roomCreate Object Used to Create Room
   * @param securityContext
   * @return created Room
   */
  Room createRoom(RoomCreate roomCreate, SecurityContextBase securityContext);

  /**
   * @param roomCreate Object Used to Create Room
   * @param securityContext
   * @return created Room unmerged
   */
  Room createRoomNoMerge(RoomCreate roomCreate, SecurityContextBase securityContext);

  /**
   * @param roomCreate Object Used to Create Room
   * @param room
   * @return if room was updated
   */
  boolean updateRoomNoMerge(Room room, RoomCreate roomCreate);

  /**
   * @param roomUpdate
   * @param securityContext
   * @return room
   */
  Room updateRoom(RoomUpdate roomUpdate, SecurityContextBase securityContext);

  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @return PaginationResponse containing paging information for Room
   */
  PaginationResponse<Room> getAllRooms(RoomFilter roomFilter, SecurityContextBase securityContext);

  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @return List of Room
   */
  List<Room> listAllRooms(RoomFilter roomFilter, SecurityContextBase securityContext);

  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @throws ResponseStatusException if roomFilter is not valid
   */
  void validate(RoomFilter roomFilter, SecurityContextBase securityContext);

  /**
   * @param roomCreate Object Used to Create Room
   * @param securityContext
   * @throws ResponseStatusException if roomCreate is not valid
   */
  void validate(RoomCreate roomCreate, SecurityContextBase securityContext);

  <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext);

  <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext);

  <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute);

  <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested);

  <T> T findByIdOrNull(Class<T> type, String id);

  void merge(java.lang.Object base);

  void massMerge(List<?> toMerge);
}
