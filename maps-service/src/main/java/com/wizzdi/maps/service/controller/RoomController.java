package com.wizzdi.maps.service.controller;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.validation.Create;
import com.wizzdi.flexicore.security.validation.Update;
import com.wizzdi.maps.model.Room;
import com.wizzdi.maps.service.request.RoomCreate;
import com.wizzdi.maps.service.request.RoomFilter;
import com.wizzdi.maps.service.request.RoomUpdate;
import com.wizzdi.maps.service.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("Room")
@Tag(name = "Room")
@OperationsInside
@Extension
public class RoomController implements Plugin {

  @Autowired private RoomService roomService;

  @PostMapping("createRoom")
  @Operation(summary = "createRoom", description = "Creates Room")
  public Room createRoom(
      @Validated(Create.class) @RequestBody RoomCreate roomCreate,
      @RequestAttribute SecurityContext securityContext) {

    return roomService.createRoom(roomCreate, securityContext);
  }

  @PutMapping("updateRoom")
  @Operation(summary = "updateRoom", description = "Updates Room")
  public Room updateRoom(
      @Validated(Update.class) @RequestBody RoomUpdate roomUpdate,
      @RequestAttribute SecurityContext securityContext) {

    return roomService.updateRoom(roomUpdate, securityContext);
  }

  @PostMapping("getAllRooms")
  @Operation(summary = "getAllRooms", description = "lists Rooms")
  public PaginationResponse<Room> getAllRooms(
      @Valid @RequestBody RoomFilter roomFilter,
      @RequestAttribute SecurityContext securityContext) {

    return roomService.getAllRooms(roomFilter, securityContext);
  }
}
