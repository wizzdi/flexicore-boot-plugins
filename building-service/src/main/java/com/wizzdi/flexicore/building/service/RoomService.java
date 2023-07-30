package com.wizzdi.flexicore.building.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.building.data.RoomRepository;
import com.wizzdi.flexicore.building.model.Room;
import com.wizzdi.flexicore.building.request.RoomCreate;
import com.wizzdi.flexicore.building.request.RoomFilter;
import com.wizzdi.flexicore.building.request.RoomUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class RoomService implements Plugin, IRoomService {

  @Autowired private RoomRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param roomCreate Object Used to Create Room
   * @param securityContext
   * @return created Room
   */
  @Override
  public Room createRoom(RoomCreate roomCreate, SecurityContextBase securityContext) {
    Room room = createRoomNoMerge(roomCreate, securityContext);
    this.repository.merge(room);
    return room;
  }

  /**
   * @param roomCreate Object Used to Create Room
   * @param securityContext
   * @return created Room unmerged
   */
  @Override
  public Room createRoomNoMerge(RoomCreate roomCreate, SecurityContextBase securityContext) {
    Room room = new Room();
    room.setId(UUID.randomUUID().toString());
    updateRoomNoMerge(room, roomCreate);

    BaseclassService.createSecurityObjectNoMerge(room, securityContext);

    return room;
  }

  /**
   * @param roomCreate Object Used to Create Room
   * @param room
   * @return if room was updated
   */
  @Override
  public boolean updateRoomNoMerge(Room room, RoomCreate roomCreate) {
    boolean update = basicService.updateBasicNoMerge(roomCreate, room);

    if (roomCreate.getExternalId() != null
        && (!roomCreate.getExternalId().equals(room.getExternalId()))) {
      room.setExternalId(roomCreate.getExternalId());
      update = true;
    }

    return update;
  }
  /**
   * @param roomUpdate
   * @param securityContext
   * @return room
   */
  @Override
  public Room updateRoom(RoomUpdate roomUpdate, SecurityContextBase securityContext) {
    Room room = roomUpdate.getRoom();
    if (updateRoomNoMerge(room, roomUpdate)) {
      this.repository.merge(room);
    }
    return room;
  }

  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @return PaginationResponse containing paging information for Room
   */
  @Override
  public PaginationResponse<Room> getAllRooms(
      RoomFilter roomFilter, SecurityContextBase securityContext) {
    List<Room> list = listAllRooms(roomFilter, securityContext);
    long count = this.repository.countAllRooms(roomFilter, securityContext);
    return new PaginationResponse<>(list, roomFilter, count);
  }

  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @return List of Room
   */
  @Override
  public List<Room> listAllRooms(RoomFilter roomFilter, SecurityContextBase securityContext) {
    return this.repository.listAllRooms(roomFilter, securityContext);
  }

  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @throws ResponseStatusException if roomFilter is not valid
   */
  @Override
  public void validate(RoomFilter roomFilter, SecurityContextBase securityContext) {
    basicService.validate(roomFilter, securityContext);
  }

  /**
   * @param roomCreate Object Used to Create Room
   * @param securityContext
   * @throws ResponseStatusException if roomCreate is not valid
   */
  @Override
  public void validate(RoomCreate roomCreate, SecurityContextBase securityContext) {
    basicService.validate(roomCreate, securityContext);
  }

  @Override
  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  @Override
  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return this.repository.findByIds(c, ids, idAttribute);
  }

  @Override
  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return this.repository.findByIds(c, requested);
  }

  @Override
  public <T> T findByIdOrNull(Class<T> type, String id) {
    return this.repository.findByIdOrNull(type, id);
  }

  @Override
  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  @Override
  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }
}
