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
import com.wizzdi.video.conference.model.RoomToVideoServerUser;
import com.wizzdi.video.conference.model.VideoServerUser;
import com.wizzdi.video.conference.service.data.RoomToVideoServerUserRepository;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserCreate;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserFilter;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserUpdate;
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
public class RoomToVideoServerUserService implements Plugin, IRoomToVideoServerUserService {

  @Autowired private RoomToVideoServerUserRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param roomToVideoServerUserCreate Object Used to Create RoomToVideoServerUser
   * @param securityContext
   * @return created RoomToVideoServerUser
   */
  @Override
  public RoomToVideoServerUser createRoomToVideoServerUser(
      RoomToVideoServerUserCreate roomToVideoServerUserCreate,
      SecurityContextBase securityContext) {
    RoomToVideoServerUser roomToVideoServerUser =
        createRoomToVideoServerUserNoMerge(roomToVideoServerUserCreate, securityContext);
    repository.merge(roomToVideoServerUser);
    return roomToVideoServerUser;
  }

  /**
   * @param roomToVideoServerUserCreate Object Used to Create RoomToVideoServerUser
   * @param securityContext
   * @return created RoomToVideoServerUser unmerged
   */
  @Override
  public RoomToVideoServerUser createRoomToVideoServerUserNoMerge(
      RoomToVideoServerUserCreate roomToVideoServerUserCreate,
      SecurityContextBase securityContext) {
    RoomToVideoServerUser roomToVideoServerUser = new RoomToVideoServerUser();
    roomToVideoServerUser.setId(UUID.randomUUID().toString());
    updateRoomToVideoServerUserNoMerge(roomToVideoServerUserCreate, roomToVideoServerUser);

    BaseclassService.createSecurityObjectNoMerge(roomToVideoServerUser, securityContext);

    return roomToVideoServerUser;
  }

  /**
   * @param roomToVideoServerUserCreate Object Used to Create RoomToVideoServerUser
   * @param roomToVideoServerUser
   * @return if roomToVideoServerUser was updated
   */
  @Override
  public boolean updateRoomToVideoServerUserNoMerge(
      RoomToVideoServerUserCreate roomToVideoServerUserCreate,
      RoomToVideoServerUser roomToVideoServerUser) {
    boolean update =
        basicService.updateBasicNoMerge(roomToVideoServerUserCreate, roomToVideoServerUser);

    if (roomToVideoServerUserCreate.getRoom() != null
        && (roomToVideoServerUser.getRoom() == null
            || !roomToVideoServerUserCreate
                .getRoom()
                .getId()
                .equals(roomToVideoServerUser.getRoom().getId()))) {
      roomToVideoServerUser.setRoom(roomToVideoServerUserCreate.getRoom());
      update = true;
    }

    if (roomToVideoServerUserCreate.getRoomUrl() != null
        && (!roomToVideoServerUserCreate.getRoomUrl().equals(roomToVideoServerUser.getRoomUrl()))) {
      roomToVideoServerUser.setRoomUrl(roomToVideoServerUserCreate.getRoomUrl());
      update = true;
    }

    if (roomToVideoServerUserCreate.getVideoServerUser() != null
        && (roomToVideoServerUser.getVideoServerUser() == null
            || !roomToVideoServerUserCreate
                .getVideoServerUser()
                .getId()
                .equals(roomToVideoServerUser.getVideoServerUser().getId()))) {
      roomToVideoServerUser.setVideoServerUser(roomToVideoServerUserCreate.getVideoServerUser());
      update = true;
    }

    return update;
  }
  /**
   * @param roomToVideoServerUserUpdate
   * @param securityContext
   * @return roomToVideoServerUser
   */
  @Override
  public RoomToVideoServerUser updateRoomToVideoServerUser(
      RoomToVideoServerUserUpdate roomToVideoServerUserUpdate,
      SecurityContextBase securityContext) {
    RoomToVideoServerUser roomToVideoServerUser =
        roomToVideoServerUserUpdate.getRoomToVideoServerUser();
    if (updateRoomToVideoServerUserNoMerge(roomToVideoServerUserUpdate, roomToVideoServerUser)) {
      repository.merge(roomToVideoServerUser);
    }
    return roomToVideoServerUser;
  }

  /**
   * @param roomToVideoServerUserFilter Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @return PaginationResponse containing paging information for RoomToVideoServerUser
   */
  @Override
  public PaginationResponse<RoomToVideoServerUser> getAllRoomToVideoServerUsers(
      RoomToVideoServerUserFilter roomToVideoServerUserFilter,
      SecurityContextBase securityContext) {
    List<RoomToVideoServerUser> list =
        listAllRoomToVideoServerUsers(roomToVideoServerUserFilter, securityContext);
    long count =
        repository.countAllRoomToVideoServerUsers(roomToVideoServerUserFilter, securityContext);
    return new PaginationResponse<>(list, roomToVideoServerUserFilter, count);
  }

  /**
   * @param roomToVideoServerUserFilter Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @return List of RoomToVideoServerUser
   */
  @Override
  public List<RoomToVideoServerUser> listAllRoomToVideoServerUsers(
      RoomToVideoServerUserFilter roomToVideoServerUserFilter,
      SecurityContextBase securityContext) {
    return repository.listAllRoomToVideoServerUsers(roomToVideoServerUserFilter, securityContext);
  }

  /**
   * @param roomToVideoServerUserFilter Object Used to List RoomToVideoServerUser
   * @param securityContext
   * @throws ResponseStatusException if roomToVideoServerUserFilter is not valid
   */
  @Override
  public void validate(
      RoomToVideoServerUserFilter roomToVideoServerUserFilter,
      SecurityContextBase securityContext) {
    basicService.validate(roomToVideoServerUserFilter, securityContext);

    Set<String> videoServerUserIds =
        roomToVideoServerUserFilter.getVideoServerUserIds() == null
            ? new HashSet<>()
            : roomToVideoServerUserFilter.getVideoServerUserIds();
    Map<String, VideoServerUser> videoServerUser =
        videoServerUserIds.isEmpty()
            ? new HashMap<>()
            : repository
                .listByIds(
                    VideoServerUser.class,
                    videoServerUserIds,
                    SecuredBasic_.security,
                    securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    videoServerUserIds.removeAll(videoServerUser.keySet());
    if (!videoServerUserIds.isEmpty()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No VideoServerUser with ids " + videoServerUserIds);
    }
    roomToVideoServerUserFilter.setVideoServerUser(new ArrayList<>(videoServerUser.values()));
    Set<String> roomIds =
        roomToVideoServerUserFilter.getRoomIds() == null
            ? new HashSet<>()
            : roomToVideoServerUserFilter.getRoomIds();
    Map<String, Room> room =
        roomIds.isEmpty()
            ? new HashMap<>()
            : repository
                .listByIds(Room.class, roomIds, SecuredBasic_.security, securityContext)
                .parallelStream()
                .collect(Collectors.toMap(f -> f.getId(), f -> f));
    roomIds.removeAll(room.keySet());
    if (!roomIds.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Room with ids " + roomIds);
    }
    roomToVideoServerUserFilter.setRoom(new ArrayList<>(room.values()));
  }

  /**
   * @param roomToVideoServerUserCreate Object Used to Create RoomToVideoServerUser
   * @param securityContext
   * @throws ResponseStatusException if roomToVideoServerUserCreate is not valid
   */
  @Override
  public void validate(
      RoomToVideoServerUserCreate roomToVideoServerUserCreate,
      SecurityContextBase securityContext) {
    basicService.validate(roomToVideoServerUserCreate, securityContext);

    String videoServerUserId = roomToVideoServerUserCreate.getVideoServerUserId();
    VideoServerUser videoServerUser =
        videoServerUserId == null
            ? null
            : repository.getByIdOrNull(
                videoServerUserId, VideoServerUser.class, SecuredBasic_.security, securityContext);
    if (videoServerUserId != null && videoServerUser == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No VideoServerUser with id " + videoServerUserId);
    }
    roomToVideoServerUserCreate.setVideoServerUser(videoServerUser);

    String roomId = roomToVideoServerUserCreate.getRoomId();
    Room room =
        roomId == null
            ? null
            : repository.getByIdOrNull(roomId, Room.class, SecuredBasic_.security, securityContext);
    if (roomId != null && room == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Room with id " + roomId);
    }
    roomToVideoServerUserCreate.setRoom(room);
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
