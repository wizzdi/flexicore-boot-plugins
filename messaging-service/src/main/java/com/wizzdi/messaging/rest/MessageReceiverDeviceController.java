package com.wizzdi.messaging.rest;

import com.flexicore.annotations.OperationsInside;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.model.MessageReceiverDevice;
import com.wizzdi.messaging.request.MessageReceiverDeviceCreate;
import com.wizzdi.messaging.request.MessageReceiverDeviceFilter;
import com.wizzdi.messaging.request.MessageReceiverDeviceUpdate;
import com.wizzdi.messaging.service.MessageReceiverDeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/messageReceiverDevice")
@Extension
@Tag(name = "messageReceiverDevice")
@OperationsInside
public class MessageReceiverDeviceController implements Plugin {

	@Autowired
	private MessageReceiverDeviceService messageReceiverDeviceService;

	@PostMapping("/createMessageReceiverDevice")
	@Operation(description = "creates MessageReceiverDevice",summary = "creates MessageReceiverDevice")
	public MessageReceiverDevice createMessageReceiverDevice( @RequestBody MessageReceiverDeviceCreate messageReceiverDeviceCreate, @RequestAttribute SecurityContext securityContext){
		messageReceiverDeviceService.validate(messageReceiverDeviceCreate,securityContext);
		return messageReceiverDeviceService.getOrCreateMessageReceiverDevice(messageReceiverDeviceCreate,securityContext);
	}

	@PostMapping("/getAllMessageReceiverDevices")
	@Operation(description = "returns MessageReceiverDevices",summary = "returns MessageReceiverDevices")

	public PaginationResponse<MessageReceiverDevice> getAllMessageReceiverDevices( @RequestBody MessageReceiverDeviceFilter messageReceiverDeviceFilter, @RequestAttribute SecurityContext securityContext){
		messageReceiverDeviceService.validate(messageReceiverDeviceFilter,securityContext);
		return messageReceiverDeviceService.getAllMessageReceiverDevices(messageReceiverDeviceFilter,securityContext);
	}

	@PutMapping("/updateMessageReceiverDevice")
	@Operation(description = "updates MessageReceiverDevice",summary = "updates MessageReceiverDevice")

	public MessageReceiverDevice updateMessageReceiverDevice( @RequestBody MessageReceiverDeviceUpdate messageReceiverDeviceUpdate, @RequestAttribute SecurityContext securityContext){
		String id=messageReceiverDeviceUpdate.getId();
		MessageReceiverDevice messageReceiverDevice=id!=null? messageReceiverDeviceService.getByIdOrNull(id,MessageReceiverDevice.class, securityContext):null;
		if(messageReceiverDevice==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no MessageReceiverDevice user with id "+id);
		}
		messageReceiverDeviceUpdate.setMessageReceiverDevice(messageReceiverDevice);
		messageReceiverDeviceService.validate(messageReceiverDeviceUpdate,securityContext);
		return messageReceiverDeviceService.updateMessageReceiverDevice(messageReceiverDeviceUpdate,securityContext);
	}
}
