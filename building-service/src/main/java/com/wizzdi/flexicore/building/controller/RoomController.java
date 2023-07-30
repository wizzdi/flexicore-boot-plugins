package com.wizzdi.flexicore.building.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.building.model.Room;
import com.wizzdi.flexicore.building.model.Room_;
import com.wizzdi.flexicore.building.request.RoomCreate;
import com.wizzdi.flexicore.building.request.RoomFilter;
import com.wizzdi.flexicore.building.request.RoomUpdate;
import com.wizzdi.flexicore.building.service.RoomService;
import com.wizzdi.flexicore.security.response.PaginationResponse;
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
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody RoomCreate roomCreate,
      @RequestAttribute SecurityContextBase securityContext) {
    roomService.validate(roomCreate, securityContext);
    return roomService.createRoom(roomCreate, securityContext);
  }

  @Operation(summary = "updateRoom", description = "Updates Room")
  @PutMapping("updateRoom")
  public Room updateRoom(
      @RequestHeader("authenticationKey") String authenticationKey,
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
      @RequestHeader("authenticationKey") String authenticationKey,
      @RequestBody RoomFilter roomFilter,
      @RequestAttribute SecurityContextBase securityContext) {
    roomService.validate(roomFilter, securityContext);
    return roomService.getAllRooms(roomFilter, securityContext);
  }
}
