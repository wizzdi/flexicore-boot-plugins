package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.messaging.model.ChatUser;

public class ChatUserUpdate extends ChatUserCreate {

	private String id;
	@JsonIgnore
	private ChatUser chatUser;

	public String getId() {
		return id;
	}

	public <T extends ChatUserUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public ChatUser getChatUser() {
		return chatUser;
	}

	public <T extends ChatUserUpdate> T setChatUser(ChatUser chatUser) {
		this.chatUser = chatUser;
		return (T) this;
	}
}
