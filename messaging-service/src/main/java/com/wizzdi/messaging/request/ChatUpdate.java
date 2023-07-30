package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.messaging.model.Chat;

public class ChatUpdate extends ChatCreate {

	private String id;
	@JsonIgnore
	private Chat chat;

	public String getId() {
		return id;
	}

	public <T extends ChatUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Chat getChat() {
		return chat;
	}

	public <T extends ChatUpdate> T setChat(Chat chat) {
		this.chat = chat;
		return (T) this;
	}
}
