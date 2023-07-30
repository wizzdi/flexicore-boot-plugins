package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;

public class ChatToChatUserCreate extends BasicCreate {

	private String chatId;
	@JsonIgnore
	private Chat chat;
	private String chatUserId;
	@JsonIgnore
	private ChatUser chatUser;
	private Boolean disabled;

	public String getChatId() {
		return chatId;
	}

	public <T extends ChatToChatUserCreate> T setChatId(String chatId) {
		this.chatId = chatId;
		return (T) this;
	}

	@JsonIgnore
	public Chat getChat() {
		return chat;
	}

	public <T extends ChatToChatUserCreate> T setChat(Chat chat) {
		this.chat = chat;
		return (T) this;
	}

	public String getChatUserId() {
		return chatUserId;
	}

	public <T extends ChatToChatUserCreate> T setChatUserId(String chatUserId) {
		this.chatUserId = chatUserId;
		return (T) this;
	}

	@JsonIgnore
	public ChatUser getChatUser() {
		return chatUser;
	}

	public <T extends ChatToChatUserCreate> T setChatUser(ChatUser chatUser) {
		this.chatUser = chatUser;
		return (T) this;
	}

	public Boolean getDisabled() {
		return disabled;
	}

	public <T extends ChatToChatUserCreate> T setDisabled(Boolean disabled) {
		this.disabled = disabled;
		return (T) this;
	}
}
