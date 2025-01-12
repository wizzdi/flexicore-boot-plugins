package com.wizzdi.video.conference.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.video.conference.model.RoomToVideoServerUser;
import com.wizzdi.video.conference.model.RoomToVideoServerUser_;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserCreate;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserFilter;
import com.wizzdi.video.conference.service.request.RoomToVideoServerUserUpdate;
import com.wizzdi.video.conference.service.service.RoomToVideoServerUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("RoomToVideoServerUser")
@Extension
@Tag(name = "RoomToVideoServerUser")
@OperationsInside
public class RoomToVideoServerUserController implements Plugin {

  @Autowired private RoomToVideoServerUserService roomToVideoServerUserService;

  @PostMapping("createRoomToVideoServerUser")
  @Operation(summary = "createRoomToVideoServerUser", description = "Creates RoomToVideoServerUser")
  public RoomToVideoServerUser createRoomToVideoServerUser(
      
      @RequestBody RoomToVideoServerUserCreate roomToVideoServerUserCreate,
      @RequestAttribute SecurityContext securityContext) {
    roomToVideoServerUserService.validate(roomToVideoServerUserCreate, securityContext);
    return roomToVideoServerUserService.createRoomToVideoServerUser(
        roomToVideoServerUserCreate, securityContext);
  }

  @Operation(summary = "updateRoomToVideoServerUser", description = "Updates RoomToVideoServerUser")
  @PutMapping("updateRoomToVideoServerUser")
  public RoomToVideoServerUser updateRoomToVideoServerUser(
      
      @RequestBody RoomToVideoServerUserUpdate roomToVideoServerUserUpdate,
      @RequestAttribute SecurityContext securityContext) {
    String roomToVideoServerUserId = roomToVideoServerUserUpdate.getId();
    RoomToVideoServerUser roomToVideoServerUser =
        roomToVideoServerUserService.getByIdOrNull(
            roomToVideoServerUserId,
            RoomToVideoServerUser.class,
            securityContext);
    if (roomToVideoServerUser == null) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "No RoomToVideoServerUser with id " + roomToVideoServerUserId);
    }
    roomToVideoServerUserUpdate.setRoomToVideoServerUser(roomToVideoServerUser);
    roomToVideoServerUserService.validate(roomToVideoServerUserUpdate, securityContext);
    return roomToVideoServerUserService.updateRoomToVideoServerUser(
        roomToVideoServerUserUpdate, securityContext);
  }

  @Operation(
      summary = "getAllRoomToVideoServerUsers",
      description = "Gets All RoomToVideoServerUsers Filtered")
  @PostMapping("getAllRoomToVideoServerUsers")
  public PaginationResponse<RoomToVideoServerUser> getAllRoomToVideoServerUsers(
      
      @RequestBody RoomToVideoServerUserFilter roomToVideoServerUserFilter,
      @RequestAttribute SecurityContext securityContext) {
    roomToVideoServerUserService.validate(roomToVideoServerUserFilter, securityContext);
    return roomToVideoServerUserService.getAllRoomToVideoServerUsers(
        roomToVideoServerUserFilter, securityContext);
  }
}
