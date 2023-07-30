package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicPropertiesFilter;
import com.wizzdi.flexicore.security.request.PaginationFilter;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MessageFilter extends PaginationFilter {

	private BasicPropertiesFilter basicPropertiesFilter;
	private Set<String> chatsIds=new HashSet<>();
	@JsonIgnore
	private List<Chat> chats;
	private Set<String> senderIds=new HashSet<>();
	@JsonIgnore
	private List<ChatUser> senders;
	private Set<String> unreadByIds=new HashSet<>();
	@JsonIgnore
	private List<ChatUser> unreadBy;
	private Set<String> addressedToIds=new HashSet<>();
	@JsonIgnore
	private List<ChatUser> addressedTo;
	private boolean lastPage;

	public BasicPropertiesFilter getBasicPropertiesFilter() {
		return basicPropertiesFilter;
	}

	public <T extends MessageFilter> T setBasicPropertiesFilter(BasicPropertiesFilter basicPropertiesFilter) {
		this.basicPropertiesFilter = basicPropertiesFilter;
		return (T) this;
	}

	public Set<String> getChatsIds() {
		return chatsIds;
	}

	public <T extends MessageFilter> T setChatsIds(Set<String> chatsIds) {
		this.chatsIds = chatsIds;
		return (T) this;
	}

	@JsonIgnore
	public List<Chat> getChats() {
		return chats;
	}

	public <T extends MessageFilter> T setChats(List<Chat> chats) {
		this.chats = chats;
		return (T) this;
	}

	public Set<String> getSenderIds() {
		return senderIds;
	}

	public <T extends MessageFilter> T setSenderIds(Set<String> senderIds) {
		this.senderIds = senderIds;
		return (T) this;
	}

	@JsonIgnore
	public List<ChatUser> getSenders() {
		return senders;
	}

	public <T extends MessageFilter> T setSenders(List<ChatUser> senders) {
		this.senders = senders;
		return (T) this;
	}

	public boolean isLastPage() {
		return lastPage;
	}

	public <T extends MessageFilter> T setLastPage(boolean lastPage) {
		this.lastPage = lastPage;
		return (T) this;
	}

	public Set<String> getUnreadByIds() {
		return unreadByIds;
	}

	public <T extends MessageFilter> T setUnreadByIds(Set<String> unreadByIds) {
		this.unreadByIds = unreadByIds;
		return (T) this;
	}

	@JsonIgnore
	public List<ChatUser> getUnreadBy() {
		return unreadBy;
	}

	public <T extends MessageFilter> T setUnreadBy(List<ChatUser> unreadBy) {
		this.unreadBy = unreadBy;
		return (T) this;
	}

	@JsonIgnore
	public List<ChatUser> getAddressedTo() {
		return addressedTo;
	}

	public <T extends MessageFilter> T setAddressedTo(List<ChatUser> addressedTo) {
		this.addressedTo = addressedTo;
		return (T) this;
	}

	public Set<String> getAddressedToIds() {
		return addressedToIds;
	}

	public <T extends MessageFilter> T setAddressedToIds(Set<String> addressedToIds) {
		this.addressedToIds = addressedToIds;
		return (T) this;
	}
}
