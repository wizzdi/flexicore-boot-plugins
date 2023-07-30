package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.messaging.model.ChatUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageReceiverDeviceFilter extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;
	private Set<String> chatUsersIds=new HashSet<>();
	@JsonIgnore
	private List<ChatUser> chatUsers;

	private Set<String> externalIds;

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends MessageReceiverDeviceFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}

	public Set<String> getChatUsersIds() {
		return chatUsersIds;
	}

	public <T extends MessageReceiverDeviceFilter> T setChatUsersIds(Set<String> chatUsersIds) {
		this.chatUsersIds = chatUsersIds;
		return (T) this;
	}

	@JsonIgnore
	public List<ChatUser> getChatUsers() {
		return chatUsers;
	}

	public <T extends MessageReceiverDeviceFilter> T setChatUsers(List<ChatUser> chatUsers) {
		this.chatUsers = chatUsers;
		return (T) this;
	}

	public Set<String> getExternalIds() {
		return externalIds;
	}

	public <T extends MessageReceiverDeviceFilter> T setExternalIds(Set<String> externalIds) {
		this.externalIds = externalIds;
		return (T) this;
	}
}
