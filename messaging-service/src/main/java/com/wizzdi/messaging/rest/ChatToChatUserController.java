package com.wizzdi.messaging.rest;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.model.ChatToChatUser;
import com.wizzdi.messaging.request.ChatToChatUserCreate;
import com.wizzdi.messaging.request.ChatToChatUserFilter;
import com.wizzdi.messaging.request.ChatToChatUserUpdate;
import com.wizzdi.messaging.service.ChatToChatUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/chatToChatUser")
@Extension
@Tag(name = "chatToChatUser")
@OperationsInside
public class ChatToChatUserController implements Plugin {

	@Autowired
	private ChatToChatUserService chatToChatUserService;

	@PostMapping("/createChatToChatUser")
	@Operation(description = "creates ChatToChatUser",summary = "creates ChatToChatUser")
	public ChatToChatUser createChatToChatUser( @RequestBody ChatToChatUserCreate chatToChatUserCreate, @RequestAttribute SecurityContext securityContext){
		chatToChatUserService.validate(chatToChatUserCreate,securityContext);
		return chatToChatUserService.createChatToChatUser(chatToChatUserCreate,securityContext);
	}

	@PostMapping("/getAllChatToChatUsers")
	@Operation(description = "returns ChatToChatUsers",summary = "returns ChatToChatUsers")

	public PaginationResponse<ChatToChatUser> getAllChatToChatUsers( @RequestBody ChatToChatUserFilter chatToChatUserFilter, @RequestAttribute SecurityContext securityContext){
		chatToChatUserService.validate(chatToChatUserFilter,securityContext);
		return chatToChatUserService.getAllChatToChatUsers(chatToChatUserFilter,securityContext);
	}

}
