package com.wizzdi.messaging.rest;

import com.flexicore.annotations.OperationsInside;

import com.flexicore.model.SecuredBasic_;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.request.ChatCreate;
import com.wizzdi.messaging.request.ChatFilter;
import com.wizzdi.messaging.request.ChatUpdate;
import com.wizzdi.messaging.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/chat")
@Extension
@Tag(name = "chat")
@OperationsInside
public class ChatController implements Plugin {

	@Autowired
	private ChatService chatService;

	@PostMapping("/createChat")
	@Operation(description = "creates Chat",summary = "creates Chat")
	public Chat createChat( @RequestBody ChatCreate chatCreate, @RequestAttribute SecurityContextBase securityContext){
		chatService.validate(chatCreate,securityContext);
		return chatService.createChat(chatCreate,securityContext);
	}

	@PostMapping("/getAllChats")
	@Operation(description = "returns Chats",summary = "returns Chats")

	public PaginationResponse<Chat> getAllChats( @RequestBody ChatFilter chatFilter, @RequestAttribute SecurityContextBase securityContext){
		chatService.validate(chatFilter,securityContext);
		return chatService.getAllChats(chatFilter,securityContext);
	}

	@PutMapping("/updateChat")
	@Operation(description = "updates Chat",summary = "updates Chat")

	public Chat updateChat( @RequestBody ChatUpdate chatUpdate, @RequestAttribute SecurityContextBase securityContext){
		String id=chatUpdate.getId();
		Chat chat=id!=null? chatService.getByIdOrNull(id,Chat.class, SecuredBasic_.security,securityContext):null;
		if(chat==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Chat user with id "+id);
		}
		chatUpdate.setChat(chat);
		chatService.validate(chatUpdate,securityContext);
		return chatService.updateChat(chatUpdate,securityContext);
	}
}
