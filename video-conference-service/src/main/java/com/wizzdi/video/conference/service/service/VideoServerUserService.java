package com.wizzdi.video.conference.service.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.video.conference.model.VideoServerUser;
import com.wizzdi.video.conference.service.data.VideoServerUserRepository;
import com.wizzdi.video.conference.service.interfaces.VideoUserProvider;
import com.wizzdi.video.conference.service.request.*;

import java.time.OffsetDateTime;
import java.util.*;
import jakarta.persistence.metamodel.SingularAttribute;

import com.wizzdi.video.conference.service.response.JitsiTokenResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.pf4j.Extension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Extension
public class VideoServerUserService implements Plugin, IVideoServerUserService {

  @Autowired private VideoServerUserRepository repository;

  @Autowired private BasicService basicService;
  @Autowired
  private ObjectProvider<VideoUserProvider<?>> videoUserProviders;
  @Value("${video.jitsi.jwt.secret}")
  private String secret;
  @Value("${video.jitsi.jwt.iss}")
  private String iss;
  @Value("${video.jitsi.jwt.aud:jitsi}")
  private String aud;
  @Value("${video.jitsi.jwt.sub}")
  private String sub;
  @Value("${video.jitsi.jwt.expMin:90}")
  private int expMin;
  @Value("${video.jitsi.jwt.emailDomain:jitsi.com}")
  private String domain;

  public VideoServerUser getVideoServerUser(SecurityContext securityContext) {
    return videoUserProviders.stream().filter(f -> f.getType().isAssignableFrom(securityContext.getUser().getClass())).findFirst().map(f -> f.getVideoServerUser(securityContext)).orElse(null);
  }

  /**
   * @param videoServerUserCreate Object Used to Create Entity_3
   * @param securityContext
   * @return created VideoServerUser
   */
  @Override
  public VideoServerUser createVideoServerUser(
      VideoServerUserCreate videoServerUserCreate, SecurityContext securityContext) {
    VideoServerUser videoServerUser =
        createVideoServerUserNoMerge(videoServerUserCreate, securityContext);
    repository.merge(videoServerUser);
    return videoServerUser;
  }

  /**
   * @param videoServerUserCreate Object Used to Create Entity_3
   * @param securityContext
   * @return created VideoServerUser unmerged
   */
  @Override
  public VideoServerUser createVideoServerUserNoMerge(
      VideoServerUserCreate videoServerUserCreate, SecurityContext securityContext) {
    VideoServerUser videoServerUser = new VideoServerUser();
    videoServerUser.setId(UUID.randomUUID().toString());
    updateVideoServerUserNoMerge(videoServerUserCreate, videoServerUser);

    BaseclassService.createSecurityObjectNoMerge(videoServerUser, securityContext);

    return videoServerUser;
  }

  /**
   * @param videoServerUserCreate Object Used to Create Entity_3
   * @param videoServerUser
   * @return if videoServerUser was updated
   */
  @Override
  public boolean updateVideoServerUserNoMerge(
      VideoServerUserCreate videoServerUserCreate, VideoServerUser videoServerUser) {
    boolean update = basicService.updateBasicNoMerge(videoServerUserCreate, videoServerUser);

    return update;
  }
  /**
   * @param videoServerUserUpdate
   * @param securityContext
   * @return videoServerUser
   */
  @Override
  public VideoServerUser updateVideoServerUser(
      VideoServerUserUpdate videoServerUserUpdate, SecurityContext securityContext) {
    VideoServerUser videoServerUser = videoServerUserUpdate.getVideoServerUser();
    if (updateVideoServerUserNoMerge(videoServerUserUpdate, videoServerUser)) {
      repository.merge(videoServerUser);
    }
    return videoServerUser;
  }

  /**
   * @param videoServerUserFilter Object Used to List Entity_3
   * @param securityContext
   * @return PaginationResponse containing paging information for VideoServerUser
   */
  @Override
  public PaginationResponse<VideoServerUser> getAllVideoServerUsers(
      VideoServerUserFilter videoServerUserFilter, SecurityContext securityContext) {
    List<VideoServerUser> list = listAllVideoServerUsers(videoServerUserFilter, securityContext);
    long count = repository.countAllVideoServerUsers(videoServerUserFilter, securityContext);
    return new PaginationResponse<>(list, videoServerUserFilter, count);
  }

  /**
   * @param videoServerUserFilter Object Used to List Entity_3
   * @param securityContext
   * @return List of VideoServerUser
   */
  @Override
  public List<VideoServerUser> listAllVideoServerUsers(
      VideoServerUserFilter videoServerUserFilter, SecurityContext securityContext) {
    return repository.listAllVideoServerUsers(videoServerUserFilter, securityContext);
  }

  /**
   * @param videoServerUserFilter Object Used to List Entity_3
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if videoServerUserFilter is not valid
   */
  @Override
  public void validate(
      VideoServerUserFilter videoServerUserFilter, SecurityContext securityContext) {
    basicService.validate(videoServerUserFilter, securityContext);
  }

  /**
   * @param videoServerUserCreate Object Used to Create Entity_3
   * @param securityContext
   * @throws org.springframework.web.server.ResponseStatusException  if videoServerUserCreate is not valid
   */
  @Override
  public void validate(
      VideoServerUserCreate videoServerUserCreate, SecurityContext securityContext) {
    basicService.validate(videoServerUserCreate, securityContext);
  }

  @Override
  public <T extends Baseclass> List<T> listByIds(
      Class<T> c, Set<String> ids, SecurityContext securityContext) {
    return repository.listByIds(c, ids, securityContext);
  }

  @Override
  public <T extends Baseclass> T getByIdOrNull(
      String id, Class<T> c, SecurityContext securityContext) {
    return repository.getByIdOrNull(id, c, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(
      String id,
      Class<T> c,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
    return repository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
  }

  @Override
  public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(
      Class<T> c,
      Set<String> ids,
      SingularAttribute<D, E> baseclassAttribute,
      SecurityContext securityContext) {
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

  public JitsiTokenResponse getJitsiToken(JitsiTokenRequest jitsiTokenRequest,SecurityContext securityContext) {
    String roomId = jitsiTokenRequest.getRoom().getId();
    OffsetDateTime expirationDate=OffsetDateTime.now().plusMinutes(expMin);
    String token = Jwts.builder()
            .claim("room", roomId)
            .setAudience(aud)
            .setSubject(sub)
            .setIssuer(iss)
            .setIssuedAt(new Date())
            .setExpiration(Date.from(expirationDate.toInstant()))
            .signWith(SignatureAlgorithm.HS256, secret)
            .claim("user", getJitsiUser(jitsiTokenRequest.getVideoServerUser()))
            .compact();
    return new JitsiTokenResponse().setToken(token).setValidTill(expirationDate);
  }

  private JitsiUser getJitsiUser(VideoServerUser videoServerUser) {
    return new JitsiUser()
            .setEmail(videoServerUser.getName()+"@"+domain)
            .setName(videoServerUser.getName());
  }

  public void validate(JitsiTokenRequest jitsiTokenRequest, SecurityContext securityContext) {

    VideoServerUser videoServerUser = getVideoServerUser(securityContext);
    if(videoServerUser==null){
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no video server user for "+securityContext.getUser());
    }
    jitsiTokenRequest.setVideoServerUser(videoServerUser);
  }
}
