package com.wizzdi.video.conference.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.video.conference.model.VideoServer;
import com.wizzdi.video.conference.service.data.VideoServerRepository;
import com.wizzdi.video.conference.service.request.VideoServerCreate;
import com.wizzdi.video.conference.service.request.VideoServerFilter;
import com.wizzdi.video.conference.service.request.VideoServerUpdate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.metamodel.SingularAttribute;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class VideoServerService implements Plugin, IVideoServerService {

  @Autowired private VideoServerRepository repository;

  @Autowired private BasicService basicService;

  /**
   * @param videoServerCreate Object Used to Create Entity_1
   * @param securityContext
   * @return created VideoServer
   */
  @Override
  public VideoServer createVideoServer(
      VideoServerCreate videoServerCreate, SecurityContextBase securityContext) {
    VideoServer videoServer = createVideoServerNoMerge(videoServerCreate, securityContext);
    repository.merge(videoServer);
    return videoServer;
  }

  /**
   * @param videoServerCreate Object Used to Create Entity_1
   * @param securityContext
   * @return created VideoServer unmerged
   */
  @Override
  public VideoServer createVideoServerNoMerge(
      VideoServerCreate videoServerCreate, SecurityContextBase securityContext) {
    VideoServer videoServer = new VideoServer();
    videoServer.setId(UUID.randomUUID().toString());
    updateVideoServerNoMerge(videoServerCreate, videoServer);

    BaseclassService.createSecurityObjectNoMerge(videoServer, securityContext);

    return videoServer;
  }

  /**
   * @param videoServerCreate Object Used to Create Entity_1
   * @param videoServer
   * @return if videoServer was updated
   */
  @Override
  public boolean updateVideoServerNoMerge(
      VideoServerCreate videoServerCreate, VideoServer videoServer) {
    boolean update = basicService.updateBasicNoMerge(videoServerCreate, videoServer);

    if (videoServerCreate.getBaseUrl() != null
        && (!videoServerCreate.getBaseUrl().equals(videoServer.getBaseUrl()))) {
      videoServer.setBaseUrl(videoServerCreate.getBaseUrl());
      update = true;
    }

    return update;
  }
  /**
   * @param videoServerUpdate
   * @param securityContext
   * @return videoServer
   */
  @Override
  public VideoServer updateVideoServer(
      VideoServerUpdate videoServerUpdate, SecurityContextBase securityContext) {
    VideoServer videoServer = videoServerUpdate.getVideoServer();
    if (updateVideoServerNoMerge(videoServerUpdate, videoServer)) {
      repository.merge(videoServer);
    }
    return videoServer;
  }

  /**
   * @param videoServerFilter Object Used to List Entity_1
   * @param securityContext
   * @return PaginationResponse containing paging information for VideoServer
   */
  @Override
  public PaginationResponse<VideoServer> getAllVideoServers(
      VideoServerFilter videoServerFilter, SecurityContextBase securityContext) {
    List<VideoServer> list = listAllVideoServers(videoServerFilter, securityContext);
    long count = repository.countAllVideoServers(videoServerFilter, securityContext);
    return new PaginationResponse<>(list, videoServerFilter, count);
  }

  /**
   * @param videoServerFilter Object Used to List Entity_1
   * @param securityContext
   * @return List of VideoServer
   */
  @Override
  public List<VideoServer> listAllVideoServers(
      VideoServerFilter videoServerFilter, SecurityContextBase securityContext) {
    return repository.listAllVideoServers(videoServerFilter, securityContext);
  }

  /**
   * @param videoServerFilter Object Used to List Entity_1
   * @param securityContext
   * @throws ResponseStatusException if videoServerFilter is not valid
   */
  @Override
  public void validate(VideoServerFilter videoServerFilter, SecurityContextBase securityContext) {
    basicService.validate(videoServerFilter, securityContext);
  }

  /**
   * @param videoServerCreate Object Used to Create Entity_1
   * @param securityContext
   * @throws ResponseStatusException if videoServerCreate is not valid
   */
  @Override
  public void validate(VideoServerCreate videoServerCreate, SecurityContextBase securityContext) {
    basicService.validate(videoServerCreate, securityContext);
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
