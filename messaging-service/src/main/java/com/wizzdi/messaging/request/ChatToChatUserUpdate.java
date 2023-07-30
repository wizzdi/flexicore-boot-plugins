package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.messaging.model.ChatToChatUser;

public class ChatToChatUserUpdate extends ChatToChatUserCreate {

	private String id;
	@JsonIgnore
	private ChatToChatUser chatToChatUser;

	public String getId() {
		return id;
	}

	public <T extends ChatToChatUserUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public ChatToChatUser getChatToChatUser() {
		return chatToChatUser;
	}

	public <T extends ChatToChatUserUpdate> T setChatToChatUser(ChatToChatUser chatToChatUser) {
		this.chatToChatUser = chatToChatUser;
		return (T) this;
	}
}
