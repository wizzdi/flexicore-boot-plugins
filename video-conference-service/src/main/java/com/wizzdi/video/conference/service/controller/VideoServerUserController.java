package com.wizzdi.video.conference.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.video.conference.model.VideoServerUser;
import com.wizzdi.video.conference.model.VideoServerUser_;
import com.wizzdi.video.conference.service.request.JitsiTokenRequest;
import com.wizzdi.video.conference.service.request.VideoServerUserCreate;
import com.wizzdi.video.conference.service.request.VideoServerUserFilter;
import com.wizzdi.video.conference.service.request.VideoServerUserUpdate;
import com.wizzdi.video.conference.service.response.JitsiTokenResponse;
import com.wizzdi.video.conference.service.service.VideoServerUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("VideoServerUser")
@Extension
@Tag(name = "VideoServerUser")
@OperationsInside
public class VideoServerUserController implements Plugin {

  @Autowired private VideoServerUserService videoServerUserService;

  @PostMapping("getJitsiToken")
  @Operation(summary = "getJitsiToken", description = "Returns Jitsi Token")
  public JitsiTokenResponse getJitsiToken(
          
          @RequestBody JitsiTokenRequest jitsiTokenRequest,
          @RequestAttribute SecurityContextBase securityContext) {
    videoServerUserService.validate(jitsiTokenRequest,securityContext);
    return videoServerUserService.getJitsiToken(jitsiTokenRequest, securityContext);
  }
  @PostMapping("createVideoServerUser")
  @Operation(summary = "createVideoServerUser", description = "Creates VideoServerUser")
  public VideoServerUser createVideoServerUser(
      
      @RequestBody VideoServerUserCreate videoServerUserCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    videoServerUserService.validate(videoServerUserCreate, securityContext);
    return videoServerUserService.createVideoServerUser(videoServerUserCreate, securityContext);
  }

  @Operation(summary = "updateVideoServerUser", description = "Updates VideoServerUser")
  @PutMapping("updateVideoServerUser")
  public VideoServerUser updateVideoServerUser(
      
      @RequestBody VideoServerUserUpdate videoServerUserUpdate,
      @RequestAttribute SecurityContextBase securityContext) {
    String videoServerUserId = videoServerUserUpdate.getId();
    VideoServerUser videoServerUser =
        videoServerUserService.getByIdOrNull(
            videoServerUserId, VideoServerUser.class, VideoServerUser_.security, securityContext);
    if (videoServerUser == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No VideoServerUser with id " + videoServerUserId);
    }
    videoServerUserUpdate.setVideoServerUser(videoServerUser);
    videoServerUserService.validate(videoServerUserUpdate, securityContext);
    return videoServerUserService.updateVideoServerUser(videoServerUserUpdate, securityContext);
  }

  @Operation(summary = "getAllVideoServerUsers", description = "Gets All VideoServerUsers Filtered")
  @PostMapping("getAllVideoServerUsers")
  public PaginationResponse<VideoServerUser> getAllVideoServerUsers(
      
      @RequestBody VideoServerUserFilter videoServerUserFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    videoServerUserService.validate(videoServerUserFilter, securityContext);
    return videoServerUserService.getAllVideoServerUsers(videoServerUserFilter, securityContext);
  }
}
