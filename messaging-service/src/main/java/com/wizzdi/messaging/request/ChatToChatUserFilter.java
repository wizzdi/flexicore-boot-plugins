package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChatToChatUserFilter extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;

	private Set<String> chatsIds =new HashSet<>();
	@JsonIgnore
	private List<Chat> chats;
	private Set<String> chatUsersIds=new HashSet<>();
	@JsonIgnore
	private List<ChatUser> chatUsers;
	private Boolean disabled;

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends ChatToChatUserFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}

	public Set<String> getChatsIds() {
		return chatsIds;
	}

	public <T extends ChatToChatUserFilter> T setChatsIds(Set<String> chatsIds) {
		this.chatsIds = chatsIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Chat> getChats() {
		return chats;
	}

	public <T extends ChatToChatUserFilter> T setChats(List<Chat> chats) {
		this.chats = chats;
		return (T) this;
	}

	public Set<String> getChatUsersIds() {
		return chatUsersIds;
	}

	public <T extends ChatToChatUserFilter> T setChatUsersIds(Set<String> chatUsersIds) {
		this.chatUsersIds = chatUsersIds;
		return (T) this;
	}

	@JsonIgnore
	public List<ChatUser> getChatUsers() {
		return chatUsers;
	}

	public <T extends ChatToChatUserFilter> T setChatUsers(List<ChatUser> chatUsers) {
		this.chatUsers = chatUsers;
		return (T) this;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public <T extends ChatToChatUserFilter> T setDisabled(Boolean disabled) {
		this.disabled = disabled;
		return (T) this;
	}
}
