package com.wizzdi.video.conference.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.video.conference.model.VideoServer;
import com.wizzdi.video.conference.service.request.VideoServerCreate;
import com.wizzdi.video.conference.service.request.VideoServerFilter;
import com.wizzdi.video.conference.service.request.VideoServerUpdate;
import java.util.List;
import java.util.Set;
import jakarta.persistence.metamodel.SingularAttribute;

public interface IVideoServerService {

  /**
   * @param videoServerCreate Object Used to Create Entity_1
   * @param securityContext
   * @return created VideoServer
   */
  VideoServer createVideoServer(
      VideoServerCreate videoServerCreate, SecurityContextBase securityContext);

  /**
   * @param videoServerCreate Object Used to Create Entity_1
   * @param securityContext
   * @return created VideoServer unmerged
   */
  VideoServer createVideoServerNoMerge(
      VideoServerCreate videoServerCreate, SecurityContextBase securityContext);

  /**
   * @param videoServerCreate Object Used to Create Entity_1
   * @param videoServer
   * @return if videoServer was updated
   */
  boolean updateVideoServerNoMerge(VideoServerCreate videoServerCreate, VideoServer videoServer);

  /**
   * @param videoServerUpdate
   * @param securityContext
   * @return videoServer
   */
  VideoServer updateVideoServer(
      VideoServerUpdate videoServerUpdate, SecurityContextBase securityContext);

  /**
   * @param videoServerFilter Object Used to List Entity_1
   * @param securityContext
   * @return PaginationResponse containing paging information for VideoServer
   */
  PaginationResponse<VideoServer> getAllVideoServers(
      VideoServerFilter videoServerFilter, SecurityContextBase securityContext);

  /**
   * @param videoServerFilter Object Used to List Entity_1
   * @param securityContext
   * @return List of VideoServer
   */
  List<VideoServer> listAllVideoServers(
      VideoServerFilter videoServerFilter, SecurityContextBase securityContext);

  /**
   * @param videoServerFilter Object Used to List Entity_1
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if videoServerFilter is not valid
   */
  void validate(VideoServerFilter videoServerFilter, SecurityContextBase securityContext);

  /**
   * @param videoServerCreate Object Used to Create Entity_1
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if videoServerCreate is not valid
   */
  void validate(VideoServerCreate videoServerCreate, SecurityContextBase securityContext);

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

  void merge(Object base);

  void massMerge(List<?> toMerge);
}
