package com.wizzdi.messaging.service;

import com.flexicore.model.Baseclass;

import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.messaging.data.ChatRepository;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.ChatUser_;
import com.wizzdi.messaging.request.ChatCreate;
import com.wizzdi.messaging.request.ChatFilter;
import com.wizzdi.messaging.request.ChatUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;

import java.util.List;
import java.util.Set;

@Extension
@Component
public class ChatService implements Plugin {



	@Autowired
	private ChatRepository chatRepository;
	@Autowired
	private BasicService basicService;

	@Autowired
	private ChatUserService chatUserService;


	public Chat createChat(ChatCreate chatCreate, SecurityContextBase securityContext) {
		Chat chat = createChatNoMerge(chatCreate, securityContext);
		chatRepository.merge(chat);
		return chat;
	}


	public Chat createChatNoMerge(ChatCreate chatCreate, SecurityContextBase securityContext) {
		Chat chat = new Chat();
		chat.setId(Baseclass.getBase64ID());
		updateChatNoMerge(chatCreate, chat);
		BaseclassService.createSecurityObjectNoMerge(chat,securityContext);
		return chat;
	}

	public boolean updateChatNoMerge(ChatCreate chatCreate, Chat chat) {
		boolean update = basicService.updateBasicNoMerge(chatCreate, chat);
		if(chatCreate.getOwner()!=null&&(chat.getOwner()==null||!chatCreate.getOwner().getId().equals(chat.getOwner().getId()))){
			chat.setOwner(chatCreate.getOwner());
			update=true;
		}
		return update;
	}

	public Chat updateChat(ChatUpdate chatUpdate, SecurityContextBase securityContext) {
		Chat Chat = chatUpdate.getChat();
		if (updateChatNoMerge(chatUpdate, Chat)) {
			chatRepository.merge(Chat);
		}
		return Chat;
	}

	public void validate(ChatCreate chatCreate, SecurityContextBase securityContext) {
		basicService.validate(chatCreate,securityContext);
		String ownerId=chatCreate.getOwnerId();
		ChatUser owner=ownerId!=null?getByIdOrNull(ownerId,ChatUser.class, ChatUser_.security,securityContext):null;
		if(ownerId!=null&&owner==null){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"no owner with id "+ownerId);
		}
		chatCreate.setOwner(owner);
		if(chatCreate.getOwner()==null){
			ChatUser chatUser= chatUserService.getChatUser(securityContext);
			if(chatUser==null){
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"cannot find chat user from user "+securityContext.getUser().getName());
			}
			chatCreate.setOwner(chatUser);
		}
	}

	public void validate(ChatFilter chatFilter, SecurityContextBase securityContext) {
		basicService.validate(chatFilter, securityContext);


	}

	public PaginationResponse<Chat> getAllChats(ChatFilter ChatFilter, SecurityContextBase securityContext) {
		List<Chat> list = listAllChats(ChatFilter, securityContext);
		long count = chatRepository.countAllChats(ChatFilter, securityContext);
		return new PaginationResponse<>(list, ChatFilter, count);
	}

	public List<Chat> listAllChats(ChatFilter ChatFilter, SecurityContextBase securityContext) {
		return chatRepository.listAllChats(ChatFilter, securityContext);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return chatRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return chatRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return chatRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return chatRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return chatRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return chatRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return chatRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		chatRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		chatRepository.massMerge(toMerge);
	}
}
