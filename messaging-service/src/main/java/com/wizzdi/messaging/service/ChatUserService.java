package com.wizzdi.messaging.service;

import com.flexicore.model.Baseclass;
import com.flexicore.model.Basic;
import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.boot.base.interfaces.Plugin;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.flexicore.security.service.BaseclassService;
import com.wizzdi.flexicore.security.service.BasicService;
import com.wizzdi.messaging.data.ChatUserRepository;
import com.wizzdi.messaging.interfaces.ChatUserProvider;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.request.ChatUserCreate;
import com.wizzdi.messaging.request.ChatUserFilter;
import com.wizzdi.messaging.request.ChatUserUpdate;
import org.pf4j.Extension;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.metamodel.SingularAttribute;

import java.util.List;
import java.util.Set;

@Extension
@Component
public class ChatUserService implements Plugin {



	@Autowired
	private ChatUserRepository chatUserRepository;
	@Autowired
	private BasicService basicService;
	@Autowired
	private ObjectProvider<ChatUserProvider<?>> chatUserProviders;

	@Autowired
	private ApplicationContext applicationContext;

	public ChatUser getChatUser(SecurityContextBase securityContext) {
		return chatUserProviders.stream().filter(f -> f.getType().isAssignableFrom(securityContext.getUser().getClass())).findFirst().map(f -> f.getChatUser(securityContext)).orElse(null);
	}

	public ChatUser createChatUser(ChatUserCreate chatUserCreate, SecurityContextBase securityContext) {
		ChatUser chatUser = createChatUserNoMerge(chatUserCreate, securityContext);
		chatUserRepository.merge(chatUser);
		return chatUser;
	}


	public ChatUser createChatUserNoMerge(ChatUserCreate chatUserCreate, SecurityContextBase securityContext) {
		ChatUser chatUser = new ChatUser();
		chatUser.setId(Baseclass.getBase64ID());
		updateChatUserNoMerge(chatUserCreate, chatUser);
		BaseclassService.createSecurityObjectNoMerge(chatUser,securityContext);
		return chatUser;
	}

	public boolean updateChatUserNoMerge(ChatUserCreate chatUserCreate, ChatUser chatUser) {
		return basicService.updateBasicNoMerge(chatUserCreate, chatUser);

	}

	public ChatUser updateChatUser(ChatUserUpdate chatUserUpdate, SecurityContextBase securityContext) {
		ChatUser ChatUser = chatUserUpdate.getChatUser();
		if (updateChatUserNoMerge(chatUserUpdate, ChatUser)) {
			chatUserRepository.merge(ChatUser);
		}
		return ChatUser;
	}

	public void validate(ChatUserCreate chatUserCreate, SecurityContextBase securityContext) {
		basicService.validate(chatUserCreate,securityContext);

	}

	public void validate(ChatUserFilter chatUserFilter, SecurityContextBase securityContext) {
		basicService.validate(chatUserFilter, securityContext);


	}


	public PaginationResponse<ChatUser> getAllChatUsers(ChatUserFilter ChatUserFilter, SecurityContextBase securityContext) {
		List<ChatUser> list = listAllChatUsers(ChatUserFilter, securityContext);
		long count = chatUserRepository.countAllChatUsers(ChatUserFilter, securityContext);
		return new PaginationResponse<>(list, ChatUserFilter, count);
	}

	public List<ChatUser> listAllChatUsers(ChatUserFilter ChatUserFilter, SecurityContextBase securityContext) {
		return chatUserRepository.listAllChatUsers(ChatUserFilter, securityContext);
	}


	public <T extends Baseclass> List<T> listByIds(Class<T> c, Set<String> ids, SecurityContextBase securityContext) {
		return chatUserRepository.listByIds(c, ids, securityContext);
	}

	public <T extends Baseclass> T getByIdOrNull(String id, Class<T> c, SecurityContextBase securityContext) {
		return chatUserRepository.getByIdOrNull(id, c, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> T getByIdOrNull(String id, Class<T> c, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return chatUserRepository.getByIdOrNull(id, c, baseclassAttribute, securityContext);
	}

	public <D extends Basic, E extends Baseclass, T extends D> List<T> listByIds(Class<T> c, Set<String> ids, SingularAttribute<D, E> baseclassAttribute, SecurityContextBase securityContext) {
		return chatUserRepository.listByIds(c, ids, baseclassAttribute, securityContext);
	}

	public <D extends Basic, T extends D> List<T> findByIds(Class<T> c, Set<String> ids, SingularAttribute<D, String> idAttribute) {
		return chatUserRepository.findByIds(c, ids, idAttribute);
	}

	public <T extends Basic> List<T> findByIds(Class<T> c, Set<String> requested) {
		return chatUserRepository.findByIds(c, requested);
	}

	public <T> T findByIdOrNull(Class<T> type, String id) {
		return chatUserRepository.findByIdOrNull(type, id);
	}

	@Transactional
	public void merge(Object base) {
		chatUserRepository.merge(base);
	}

	@Transactional
	public void massMerge(List<?> toMerge) {
		chatUserRepository.massMerge(toMerge);
	}
}
