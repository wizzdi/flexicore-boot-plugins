package com.wizzdi.video.conference.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.video.conference.model.VideoServer;
import com.wizzdi.video.conference.model.VideoServer_;
import com.wizzdi.video.conference.service.request.VideoServerCreate;
import com.wizzdi.video.conference.service.request.VideoServerFilter;
import com.wizzdi.video.conference.service.request.VideoServerUpdate;
import com.wizzdi.video.conference.service.service.VideoServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("VideoServer")
@Extension
@Tag(name = "VideoServer")
@OperationsInside
public class VideoServerController implements Plugin {

  @Autowired private VideoServerService videoServerService;

  @PostMapping("createVideoServer")
  @Operation(summary = "createVideoServer", description = "Creates VideoServer")
  public VideoServer createVideoServer(
      
      @RequestBody VideoServerCreate videoServerCreate,
      @RequestAttribute SecurityContext securityContext) {
    videoServerService.validate(videoServerCreate, securityContext);
    return videoServerService.createVideoServer(videoServerCreate, securityContext);
  }

  @Operation(summary = "updateVideoServer", description = "Updates VideoServer")
  @PutMapping("updateVideoServer")
  public VideoServer updateVideoServer(
      
      @RequestBody VideoServerUpdate videoServerUpdate,
      @RequestAttribute SecurityContext securityContext) {
    String videoServerId = videoServerUpdate.getId();
    VideoServer videoServer =
        videoServerService.getByIdOrNull(
            videoServerId, VideoServer.class,  securityContext);
    if (videoServer == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No VideoServer with id " + videoServerId);
    }
    videoServerUpdate.setVideoServer(videoServer);
    videoServerService.validate(videoServerUpdate, securityContext);
    return videoServerService.updateVideoServer(videoServerUpdate, securityContext);
  }

  @Operation(summary = "getAllVideoServers", description = "Gets All VideoServers Filtered")
  @PostMapping("getAllVideoServers")
  public PaginationResponse<VideoServer> getAllVideoServers(
      
      @RequestBody VideoServerFilter videoServerFilter,
      @RequestAttribute SecurityContext securityContext) {
    videoServerService.validate(videoServerFilter, securityContext);
    return videoServerService.getAllVideoServers(videoServerFilter, securityContext);
  }
}
