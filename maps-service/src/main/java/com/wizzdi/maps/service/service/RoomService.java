package com.wizzdi.maps.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.maps.model.BuildingFloor;
import com.wizzdi.maps.model.Room;
import com.wizzdi.maps.service.data.RoomRepository;
import com.wizzdi.maps.service.request.RoomCreate;
import com.wizzdi.maps.service.request.RoomFilter;
import com.wizzdi.maps.service.request.RoomUpdate;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Extension
public class RoomService implements Plugin {

  @Autowired private RoomRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param roomCreate Object Used to Create Room
   * @param securityContext
   * @return created Room
   */
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
  public boolean updateRoomNoMerge(Room room, RoomCreate roomCreate) {
    boolean update = basicService.updateBasicNoMerge(roomCreate, room);

    if (roomCreate.getBuildingFloor() != null && (room.getBuildingFloor() == null || !roomCreate.getBuildingFloor().getId().equals(room.getBuildingFloor().getId()))) {
      room.setBuildingFloor(roomCreate.getBuildingFloor());
      update = true;
    }

    if (roomCreate.getExternalId() != null
            && (!roomCreate.getExternalId().equals(room.getExternalId()))) {
      room.setExternalId(roomCreate.getExternalId());
      update = true;
    }

    if (roomCreate.getZ() != null && (!roomCreate.getZ().equals(room.getZ()))) {
      room.setZ(roomCreate.getZ());
      update = true;
    }

    if (roomCreate.getX() != null && (!roomCreate.getX().equals(room.getX()))) {
      room.setX(roomCreate.getX());
      update = true;
    }

    if (roomCreate.getY() != null && (!roomCreate.getY().equals(room.getY()))) {
      room.setY(roomCreate.getY());
      update = true;
    }

    return update;
  }
  /**
   * @param roomUpdate
   * @param securityContext
   * @return room
   */
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
  public PaginationResponse<Room> getAllRooms(
      RoomFilter roomFilter, SecurityContextBase securityContext) {
    List<Room> list = listAllRooms(roomFilter, securityContext);
    long count = this.repository.countAllRooms(roomFilter, securityContext);
    return new PaginationResponse<>(list, roomFilter.getPageSize(), count);
  }

  /**
   * @param roomFilter Object Used to List Room
   * @param securityContext
   * @return List of Room
   */
  public List<Room> listAllRooms(RoomFilter roomFilter, SecurityContextBase securityContext) {
    return this.repository.listAllRooms(roomFilter, securityContext);
  }

  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, securityContext);
  }

  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return this.repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return this.repository.findByIds(c, ids, idAttribute);
  }

  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return this.repository.findByIds(c, requested);
  }

  public <T> T findByIdOrNull(Class<T> type, String id) {
    return this.repository.findByIdOrNull(type, id);
  }

  public void merge(java.lang.Object base) {
    this.repository.merge(base);
  }

  public void massMerge(List<?> toMerge) {
    this.repository.massMerge(toMerge);
  }

  public Room getOrCreateByExternalId(BuildingFloor buildingFloor, String roomId, SecurityContextBase gatewaySecurityContext) {
    String externalId= "%s_%s".formatted(gatewaySecurityContext.getTenantToCreateIn().getId(), roomId);
    return listAllRooms(new RoomFilter().setExternalId(Collections.singleton(externalId)),null).stream().findFirst().orElseGet(()->createRoom(new RoomCreate().setBuildingFloor(buildingFloor).setExternalId(externalId).setName(roomId),gatewaySecurityContext));
  }
}
