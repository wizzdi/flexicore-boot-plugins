package com.wizzdi.messaging.firebase.controller;

import com.flexicore.annotations.OperationsInside;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;

import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.connectors.firebase.model.FirebaseEnabledDevice;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceCreate;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceFilter;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceUpdate;
import com.wizzdi.messaging.firebase.service.FirebaseEnabledDeviceService;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.service.ChatUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/firebaseEnabledDevice")
@Extension
@Tag(name = "firebaseEnabledDevice")
@OperationsInside
public class FirebaseEnabledDeviceController implements Plugin {

	@Autowired
	private FirebaseEnabledDeviceService firebaseEnabledDeviceService;
	@Autowired
	private ChatUserService chatUserService;

	@PostMapping("/createFirebaseEnabledDevice")
	@Operation(description = "creates FirebaseEnabledDevice",summary = "creates FirebaseEnabledDevice")
	public FirebaseEnabledDevice createFirebaseEnabledDevice( @RequestBody FirebaseEnabledDeviceCreate firebaseEnabledDeviceCreate, @RequestAttribute SecurityContext securityContext){
		firebaseEnabledDeviceService.validate(firebaseEnabledDeviceCreate,securityContext);
		return firebaseEnabledDeviceService.getOrCreateFirebaseEnabledDevice(firebaseEnabledDeviceCreate,securityContext);
	}

	@PostMapping("/getAllFirebaseEnabledDevices")
	@Operation(description = "returns FirebaseEnabledDevices",summary = "returns FirebaseEnabledDevices")

	public PaginationResponse<FirebaseEnabledDevice> getAllFirebaseEnabledDevices( @RequestBody FirebaseEnabledDeviceFilter firebaseEnabledDeviceFilter, @RequestAttribute SecurityContext securityContext){
		firebaseEnabledDeviceService.validate(firebaseEnabledDeviceFilter,securityContext);
		return firebaseEnabledDeviceService.getAllFirebaseEnabledDevices(firebaseEnabledDeviceFilter,securityContext);
	}

	@PutMapping("/updateFirebaseEnabledDevice")
	@Operation(description = "updates FirebaseEnabledDevice",summary = "updates FirebaseEnabledDevice")

	public FirebaseEnabledDevice updateFirebaseEnabledDevice( @RequestBody FirebaseEnabledDeviceUpdate firebaseEnabledDeviceUpdate, @RequestAttribute SecurityContext securityContext){
		String id=firebaseEnabledDeviceUpdate.getId();
		FirebaseEnabledDevice firebaseEnabledDevice=id!=null? firebaseEnabledDeviceService.getByIdOrNull(id,FirebaseEnabledDevice.class, securityContext):null;
		if(firebaseEnabledDevice==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no FirebaseEnabledDevice with id "+id);
		}
		ChatUser chatUser = chatUserService.getChatUser(securityContext);
		if(chatUser==null){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"no chat user from  "+securityContext.getUser().getId());

		}
		if(!firebaseEnabledDevice.getOwner().getId().equals(chatUser.getId())){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"not allowed to change firebaseEnabledDevice "+id);

		}
		firebaseEnabledDeviceService.validate(firebaseEnabledDeviceUpdate,securityContext);
		firebaseEnabledDeviceUpdate.setFirebaseEnabledDevice(firebaseEnabledDevice);
		return firebaseEnabledDeviceService.updateFirebaseEnabledDevice(firebaseEnabledDeviceUpdate,securityContext);
	}
}
