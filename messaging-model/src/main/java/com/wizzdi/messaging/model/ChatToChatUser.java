package com.wizzdi.messaging.model;

import com.flexicore.model.Basic;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class ChatToChatUser extends Basic {


	private boolean pendingInvite;
	@ManyToOne(targetEntity = Chat.class)
	private Chat chat;
	@ManyToOne(targetEntity = ChatUser.class)
	private ChatUser chatUser;
	private boolean disabled;

	@ManyToOne(targetEntity = Chat.class)
	public Chat getChat() {
		return chat;
	}

	public <T extends ChatToChatUser> T setChat(Chat chat) {
		this.chat = chat;
		return (T) this;
	}

	@ManyToOne(targetEntity = ChatUser.class)
	public ChatUser getChatUser() {
		return chatUser;
	}

	public <T extends ChatToChatUser> T setChatUser(ChatUser participant) {
		this.chatUser = participant;
		return (T) this;
	}

	public boolean isPendingInvite() {
		return pendingInvite;
	}

	public <T extends ChatToChatUser> T setPendingInvite(boolean pendingInvite) {
		this.pendingInvite = pendingInvite;
		return (T) this;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public <T extends ChatToChatUser> T setDisabled(boolean disabled) {
		this.disabled = disabled;
		return (T) this;
	}
}
