package com.wizzdi.video.conference.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.video.conference.model.Room;
import com.wizzdi.video.conference.model.Room_;
import com.wizzdi.video.conference.service.request.RoomCreate;
import com.wizzdi.video.conference.service.request.RoomFilter;
import com.wizzdi.video.conference.service.request.RoomUpdate;
import com.wizzdi.video.conference.service.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("Room")
@Extension
@Tag(name = "Room")
@OperationsInside
public class RoomController implements Plugin {

  @Autowired private RoomService roomService;

  @PostMapping("createRoom")
  @Operation(summary = "createRoom", description = "Creates Room")
  public Room createRoom(
      
      @RequestBody RoomCreate roomCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    roomService.validate(roomCreate, securityContext);
    return roomService.createRoom(roomCreate, securityContext);
  }

  @Operation(summary = "updateRoom", description = "Updates Room")
  @PutMapping("updateRoom")
  public Room updateRoom(
      
      @RequestBody RoomUpdate roomUpdate,
      @RequestAttribute SecurityContextBase securityContext) {
    String roomId = roomUpdate.getId();
    Room room = roomService.getByIdOrNull(roomId, Room.class, Room_.security, securityContext);
    if (room == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Room with id " + roomId);
    }
    roomUpdate.setRoom(room);
    roomService.validate(roomUpdate, securityContext);
    return roomService.updateRoom(roomUpdate, securityContext);
  }

  @Operation(summary = "getAllRooms", description = "Gets All Rooms Filtered")
  @PostMapping("getAllRooms")
  public PaginationResponse<Room> getAllRooms(
      
      @RequestBody RoomFilter roomFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    roomService.validate(roomFilter, securityContext);
    return roomService.getAllRooms(roomFilter, securityContext);
  }
}
