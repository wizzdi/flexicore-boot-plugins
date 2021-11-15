package com.wizzdi.video.conference.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.video.conference.model.VideoServerUser;
import com.wizzdi.video.conference.service.request.VideoServerUserCreate;
import com.wizzdi.video.conference.service.request.VideoServerUserFilter;
import com.wizzdi.video.conference.service.request.VideoServerUserUpdate;
import java.util.List;
import java.util.Set;
import javax.persistence.metamodel.SingularAttribute;

public interface IVideoServerUserService {

  /**
   * @param videoServerUserCreate Object Used to Create Entity_3
   * @param securityContext
   * @return created VideoServerUser
   */
  VideoServerUser createVideoServerUser(
      VideoServerUserCreate videoServerUserCreate, SecurityContextBase securityContext);

  /**
   * @param videoServerUserCreate Object Used to Create Entity_3
   * @param securityContext
   * @return created VideoServerUser unmerged
   */
  VideoServerUser createVideoServerUserNoMerge(
      VideoServerUserCreate videoServerUserCreate, SecurityContextBase securityContext);

  /**
   * @param videoServerUserCreate Object Used to Create Entity_3
   * @param videoServerUser
   * @return if videoServerUser was updated
   */
  boolean updateVideoServerUserNoMerge(
      VideoServerUserCreate videoServerUserCreate, VideoServerUser videoServerUser);

  /**
   * @param videoServerUserUpdate
   * @param securityContext
   * @return videoServerUser
   */
  VideoServerUser updateVideoServerUser(
      VideoServerUserUpdate videoServerUserUpdate, SecurityContextBase securityContext);

  /**
   * @param videoServerUserFilter Object Used to List Entity_3
   * @param securityContext
   * @return PaginationResponse containing paging information for VideoServerUser
   */
  PaginationResponse<VideoServerUser> getAllVideoServerUsers(
      VideoServerUserFilter videoServerUserFilter, SecurityContextBase securityContext);

  /**
   * @param videoServerUserFilter Object Used to List Entity_3
   * @param securityContext
   * @return List of VideoServerUser
   */
  List<VideoServerUser> listAllVideoServerUsers(
      VideoServerUserFilter videoServerUserFilter, SecurityContextBase securityContext);

  /**
   * @param videoServerUserFilter Object Used to List Entity_3
   * @param securityContext
   * @throws ResponseStatusException if videoServerUserFilter is not valid
   */
  void validate(VideoServerUserFilter videoServerUserFilter, SecurityContextBase securityContext);

  /**
   * @param videoServerUserCreate Object Used to Create Entity_3
   * @param securityContext
   * @throws ResponseStatusException if videoServerUserCreate is not valid
   */
  void validate(VideoServerUserCreate videoServerUserCreate, SecurityContextBase securityContext);

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
