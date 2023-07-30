package com.wizzdi.video.conference.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.video.conference.model.Room;
import com.wizzdi.video.conference.model.VideoServer;
import com.wizzdi.video.conference.service.data.RoomRepository;
import com.wizzdi.video.conference.service.request.RoomCreate;
import com.wizzdi.video.conference.service.request.RoomFilter;
import com.wizzdi.video.conference.service.request.RoomUpdate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class RoomService implements Plugin, IRoomService {

  @Autowired private RoomRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param roomCreate Object Used to Create Entity_4
   * @param securityContext
   * @return created Room
   */
  @Override
  public Room createRoom(RoomCreate roomCreate, SecurityContextBase securityContext) {
    Room room = createRoomNoMerge(roomCreate, securityContext);
    repository.merge(room);
    return room;
  }

  /**
   * @param roomCreate Object Used to Create Entity_4
   * @param securityContext
   * @return created Room unmerged
   */
  @Override
  public Room createRoomNoMerge(RoomCreate roomCreate, SecurityContextBase securityContext) {
    Room room = new Room();
    room.setId(UUID.randomUUID().toString());
    updateRoomNoMerge(roomCreate, room);

    BaseclassService.createSecurityObjectNoMerge(room, securityContext);

    return room;
  }

  /**
   * @param roomCreate Object Used to Create Entity_4
   * @param room
   * @return if room was updated
   */
  @Override
  public boolean updateRoomNoMerge(RoomCreate roomCreate, Room room) {
    boolean update = basicService.updateBasicNoMerge(roomCreate, room);

    if (roomCreate.getVideoServer() != null
        && (room.getVideoServer() == null
            || !roomCreate.getVideoServer().getId().equals(room.getVideoServer().getId()))) {
      room.setVideoServer(roomCreate.getVideoServer());
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
    if (updateRoomNoMerge(roomUpdate, room)) {
      repository.merge(room);
    }
    return room;
  }

  /**
   * @param roomFilter Object Used to List Entity_4
   * @param securityContext
   * @return PaginationResponse containing paging information for Room
   */
  @Override
  public PaginationResponse<Room> getAllRooms(
      RoomFilter roomFilter, SecurityContextBase securityContext) {
    List<Room> list = listAllRooms(roomFilter, securityContext);
    long count = repository.countAllRooms(roomFilter, securityContext);
    return new PaginationResponse<>(list, roomFilter, count);
  }

  /**
   * @param roomFilter Object Used to List Entity_4
   * @param securityContext
   * @return List of Room
   */
  @Override
  public List<Room> listAllRooms(RoomFilter roomFilter, SecurityContextBase securityContext) {
    return repository.listAllRooms(roomFilter, securityContext);
  }

  /**
   * @param roomFilter Object Used to List Entity_4
   * @param securityContext
   * @throws ResponseStatusException if roomFilter is not valid
   */
  @Override
  public void validate(RoomFilter roomFilter, SecurityContextBase securityContext) {
    basicService.validate(roomFilter, securityContext);

    Set<String> videoServerIds =
        roomFilter.getVideoServerIds() == null ? new HashSet<>() : roomFilter.getVideoServerIds();
    Map<String, VideoServer> videoServer =
        videoServerIds.isEmpty()
            ? new HashMap<>()
            : repository
                .listByIds(
                    VideoServer.class, videoServerIds, SecuredBasic_.security, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    videoServerIds.removeAll(videoServer.keySet());
    if (!videoServerIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No VideoServer with ids " + videoServerIds);
    }
    roomFilter.setVideoServer(new ArrayList<>(videoServer.values()));
  }

  /**
   * @param roomCreate Object Used to Create Entity_4
   * @param securityContext
   * @throws ResponseStatusException if roomCreate is not valid
   */
  @Override
  public void validate(RoomCreate roomCreate, SecurityContextBase securityContext) {
    basicService.validate(roomCreate, securityContext);

    String videoServerId = roomCreate.getVideoServerId();
    VideoServer videoServer =
        videoServerId == null
            ? null
            : repository.getByIdOrNull(
                videoServerId, VideoServer.class, SecuredBasic_.security, securityContext);
    if (videoServerId != null && videoServer == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No VideoServer with id " + videoServerId);
    }
    roomCreate.setVideoServer(videoServer);
  }

  @Override
  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
    return repository.listByIds(c, ids, securityContext);
  }

  @Override
  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContextBase securityContext) {
    return repository.getByIdOrNull(id, c, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContextBase securityContext) {
    return repository.listByIds(c, ids, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, T extends D> List<T> findByIds(
      Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
    return repository.findByIds(c, ids, idAttribute);
  }

  @Override
  public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
    return repository.findByIds(c, requested);
  }

  @Override
  public <T> T findByIdOrNull(Class<T> type, String id) {
    return repository.findByIdOrNull(type, id);
  }

  @Override
  public void merge(Object base) {
    repository.merge(base);
  }

  @Override
  public void massMerge(List<?> toMerge) {
    repository.massMerge(toMerge);
  }
}
