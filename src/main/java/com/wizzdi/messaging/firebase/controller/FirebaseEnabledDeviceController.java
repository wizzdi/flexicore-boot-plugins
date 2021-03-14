package com.wizzdi.messaging.firebase.controller;

import com.flexicore.annotations.OperationsInside;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;

import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.connectors.firebase.model.FirebaseEnabledDevice;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceCreate;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceFilter;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceUpdate;
import com.wizzdi.messaging.firebase.service.FirebaseEnabledDeviceService;
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

	@PostMapping("/createFirebaseEnabledDevice")
	@Operation(description = "creates FirebaseEnabledDevice",summary = "creates FirebaseEnabledDevice")
	public FirebaseEnabledDevice createFirebaseEnabledDevice(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody FirebaseEnabledDeviceCreate firebaseEnabledDeviceCreate, @RequestAttribute SecurityContextBase securityContext){
		firebaseEnabledDeviceService.validate(firebaseEnabledDeviceCreate,securityContext);
		return firebaseEnabledDeviceService.createFirebaseEnabledDevice(firebaseEnabledDeviceCreate,securityContext);
	}

	@PostMapping("/getAllFirebaseEnabledDevices")
	@Operation(description = "returns FirebaseEnabledDevices",summary = "returns FirebaseEnabledDevices")

	public PaginationResponse<FirebaseEnabledDevice> getAllFirebaseEnabledDevices(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody FirebaseEnabledDeviceFilter firebaseEnabledDeviceFilter, @RequestAttribute SecurityContextBase securityContext){
		firebaseEnabledDeviceService.validate(firebaseEnabledDeviceFilter,securityContext);
		return firebaseEnabledDeviceService.getAllFirebaseEnabledDevices(firebaseEnabledDeviceFilter,securityContext);
	}

	@PutMapping("/updateFirebaseEnabledDevice")
	@Operation(description = "updates FirebaseEnabledDevice",summary = "updates FirebaseEnabledDevice")

	public FirebaseEnabledDevice updateFirebaseEnabledDevice(@RequestHeader(value = "authenticationKey",required = false)String key, @RequestBody FirebaseEnabledDeviceUpdate firebaseEnabledDeviceUpdate, @RequestAttribute SecurityContextBase securityContext){
		String id=firebaseEnabledDeviceUpdate.getId();
		FirebaseEnabledDevice firebaseEnabledDevice=id!=null? firebaseEnabledDeviceService.findByIdOrNull(FirebaseEnabledDevice.class,id):null;
		if(firebaseEnabledDevice==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no FirebaseEnabledDevice with id "+id);
		}
		if(!firebaseEnabledDevice.getOwner().getId().equals(securityContext.getUser().getId())){
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"not allowed to change firebaseEnabledDevice "+id);

		}
		firebaseEnabledDeviceUpdate.setFirebaseEnabledDevice(firebaseEnabledDevice);
		firebaseEnabledDeviceService.validate(firebaseEnabledDeviceUpdate,securityContext);
		return firebaseEnabledDeviceService.updateFirebaseEnabledDevice(firebaseEnabledDeviceUpdate,securityContext);
	}
}
