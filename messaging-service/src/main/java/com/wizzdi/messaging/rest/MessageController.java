package com.wizzdi.messaging.rest;

import com.flexicore.annotations.OperationsInside;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.MarkMessagesRequest;
import com.wizzdi.messaging.request.MessageCreate;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.request.MessageUpdate;
import com.wizzdi.messaging.response.UnreadMessagesSummary;
import com.wizzdi.messaging.service.ChatUserService;
import com.wizzdi.messaging.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/message")
@Extension
@Tag(name = "message")
@OperationsInside
public class MessageController implements Plugin {

	@Autowired
	private MessageService messageService;
	@Autowired
	private ChatUserService chatUserService;

	@PostMapping("/createMessage")
	@Operation(description = "creates Message",summary = "creates Message")
	public Message createMessage( @RequestBody MessageCreate messageCreate, @RequestAttribute SecurityContext securityContext){
		messageService.validate(messageCreate,securityContext);
		return messageService.createMessage(messageCreate,securityContext);
	}

	@PostMapping("/markRead")
	@Operation(description = "marks messages as read",summary = "marks messages as read")

	public PaginationResponse<Message> markRead( @RequestBody MarkMessagesRequest markMessagesRequest, @RequestAttribute SecurityContext securityContext){
		messageService.validate(markMessagesRequest,securityContext);
		return messageService.markRead(markMessagesRequest,securityContext);
	}

	@PostMapping("/getMessageSummary")
	@Operation(description = "getMessageSummary",summary = "getMessageSummary")

	public UnreadMessagesSummary getMessageSummary( @RequestBody MessageFilter messageFilter, @RequestAttribute SecurityContext securityContext){
		messageService.validate(messageFilter,securityContext);
		return messageService.getMessageSummary(messageFilter,securityContext);
	}

	@PostMapping("/getAllMessages")
	@Operation(description = "returns Messages",summary = "returns Messages")

	public PaginationResponse<Message> getAllMessages( @RequestBody MessageFilter messageFilter, @RequestAttribute SecurityContext securityContext){
		messageService.validate(messageFilter,securityContext);
		return messageService.getAllMessages(messageFilter,securityContext);
	}

	@PutMapping("/updateMessage")
	@Operation(description = "updates Message",summary = "updates Message")

	public Message updateMessage( @RequestBody MessageUpdate messageUpdate, @RequestAttribute SecurityContext securityContext){
		String id=messageUpdate.getId();
		Message message=id!=null? messageService.findByIdOrNull(Message.class,id):null;
		if(message==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no Message with id "+id);
		}
		ChatUser chatUser = chatUserService.getChatUser(securityContext);
		if(!message.getSender().getId().equals(chatUser.getId())){
			if(messageUpdate.getChatUsers()==null||messageUpdate.getChatUsers().isEmpty()||!message.getChatUsers().containsKey(chatUser.getId())){
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"non sender users may change only the message read date and that was not sent");

			}

		}
		messageUpdate.setMessage(message);
		messageService.validate(messageUpdate,securityContext);
		return messageService.updateMessage(messageUpdate,securityContext);
	}
}
