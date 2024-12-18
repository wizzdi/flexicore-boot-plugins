package com.wizzdi.messaging.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.messaging.data.ChatToChatUserRepository;
import com.wizzdi.messaging.model.*;
import com.wizzdi.messaging.request.ChatToChatUserCreate;
import com.wizzdi.messaging.request.ChatToChatUserFilter;
import com.wizzdi.messaging.request.ChatToChatUserUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.metamodel.SingularAttribute;
import java.util.*;
import java.util.stream.Collectors;

@Extension
@Component
public class ChatToChatUserService implements Plugin {


	@Autowired
	private ChatToChatUserRepository chatToChatUserRepository;
	@Autowired
	private BasicService basicService;


	public ChatToChatUser createChatToChatUser(ChatToChatUserCreate chatToChatUserCreate, SecurityContext securityContext) {
		ChatToChatUser chatToChatUser = createChatToChatUserNoMerge(chatToChatUserCreate, securityContext);
		chatToChatUserRepository.merge(chatToChatUser);
		return chatToChatUser;
	}

	public void merge(Object o) {
		chatToChatUserRepository.merge(o);
	}

	public void massMerge(List<Object> list) {
		chatToChatUserRepository.massMerge(list);
	}

	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContext securityContext) {
		return chatToChatUserRepository.listByIds(c, ids, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return chatToChatUserRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public ChatToChatUser createChatToChatUserNoMerge(ChatToChatUserCreate chatToChatUserCreate, SecurityContext securityContext) {
		ChatToChatUser chatToChatUser = new ChatToChatUser();
		chatToChatUser.setId(UUID.randomUUID().toString());
		updateChatToChatUserNoMerge(chatToChatUserCreate, chatToChatUser);
		return chatToChatUser;
	}

	public boolean updateChatToChatUserNoMerge(ChatToChatUserCreate chatToChatUserCreate, ChatToChatUser chatToChatUser) {
		boolean update = basicService.updateBasicNoMerge(chatToChatUserCreate, chatToChatUser);
		if (chatToChatUserCreate.getChatUser() != null && (chatToChatUser.getChatUser() == null || !chatToChatUserCreate.getChatUser().getId().equals(chatToChatUser.getChatUser().getId()))) {
			chatToChatUser.setChatUser(chatToChatUserCreate.getChatUser());
			update = true;
		}

		if (chatToChatUserCreate.getChat() != null && (chatToChatUser.getChat() == null || !chatToChatUserCreate.getChat().getId().equals(chatToChatUser.getChat().getId()))) {
			chatToChatUser.setChat(chatToChatUserCreate.getChat());
			update = true;
		}
		if (chatToChatUserCreate.getDisabled() != null && !chatToChatUserCreate.getDisabled().equals(chatToChatUser.isDisabled())) {
			chatToChatUser.setDisabled(chatToChatUserCreate.getDisabled());
			update = true;
		}
		return update;
	}

	public ChatToChatUser updateChatToChatUser(ChatToChatUserUpdate chatToChatUserUpdate, SecurityContext securityContext) {
		ChatToChatUser ChatToChatUser = chatToChatUserUpdate.getChatToChatUser();
		if (updateChatToChatUserNoMerge(chatToChatUserUpdate, ChatToChatUser)) {
			chatToChatUserRepository.merge(ChatToChatUser);
		}
		return ChatToChatUser;
	}

	public void validate(ChatToChatUserCreate chatToChatUserCreate, SecurityContext securityContext) {
		basicService.validate(chatToChatUserCreate, securityContext);
		String chatId = chatToChatUserCreate.getChatId();
		Chat chat = chatId != null ? getByIdOrNull(chatId, Chat.class,securityContext) : null;
		if (chatId != null && chat == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chat with id " + chatId);
		}
		chatToChatUserCreate.setChat(chat);

		String chatUserId = chatToChatUserCreate.getChatUserId();
		ChatUser chatUser = chatUserId != null ? getByIdOrNull(chatUserId, ChatUser.class,securityContext) : null;
		if (chatUserId != null && chatUser == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chatUser with id " + chatUserId);
		}
		chatToChatUserCreate.setChatUser(chatUser);

	}

	public void validate(ChatToChatUserFilter chatToChatUserFilter, SecurityContext securityContext) {
		basicService.validate(chatToChatUserFilter, securityContext);
		Set<String> chatIds=chatToChatUserFilter.getChatsIds();
		Map<String,Chat> chatMap=chatIds.isEmpty()?new HashMap<>():chatToChatUserRepository.listByIds(Chat.class,chatIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
		chatIds.removeAll(chatMap.keySet());
		if(!chatIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chats with ids " + chatIds);
		}
		chatToChatUserFilter.setChats(new ArrayList<>(chatMap.values()));

		Set<String> chatUserIds=chatToChatUserFilter.getChatUsersIds();
		Map<String,ChatUser> chatUserMap=chatUserIds.isEmpty()?new HashMap<>():listByIds(ChatUser.class,chatUserIds,securityContext).stream().collect(Collectors.toMap(f->f.getId(),f->f));
		chatUserIds.removeAll(chatUserMap.keySet());
		if(!chatUserIds.isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "no chat users with ids " + chatUserIds);
		}
		chatToChatUserFilter.setChatUsers(new ArrayList<>(chatUserMap.values()));

		if(chatToChatUserFilter.getChatUsers().isEmpty()&&chatToChatUserFilter.getChats().isEmpty()){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"must specify at least one chat or chat user");
		}



	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContext securityContext) {
		return chatToChatUserRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContext securityContext) {
		return chatToChatUserRepository.getByIdOrNull(id, c, securityContext);
	}

	public PaginationResponse<ChatToChatUser> getAllChatToChatUsers(ChatToChatUserFilter ChatToChatUserFilter, SecurityContext securityContext) {
		List<ChatToChatUser> list = listAllChatToChatUsers(ChatToChatUserFilter, securityContext);
		long count = chatToChatUserRepository.countAllChatToChatUsers(ChatToChatUserFilter, securityContext);
		return new PaginationResponse<>(list, ChatToChatUserFilter, count);
	}

	public List<ChatToChatUser> listAllChatToChatUsers(ChatToChatUserFilter ChatToChatUserFilter, SecurityContext securityContext) {
		return chatToChatUserRepository.listAllChatToChatUsers(ChatToChatUserFilter, securityContext);
	}

	public <T extends Baseclass> List<T> findByIds(Class<T> c, Set<String> requested) {
		return chatToChatUserRepository.findByIds(c, requested);
	}

}
