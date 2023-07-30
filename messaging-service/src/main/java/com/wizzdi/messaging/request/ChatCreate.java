package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.messaging.model.ChatUser;

public class ChatCreate extends BasicCreate {

	private String ownerId;
	@JsonIgnore
	private ChatUser owner;

	public String getOwnerId() {
		return ownerId;
	}

	public <T extends ChatCreate> T setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		return (T) this;
	}

	@JsonIgnore
	public ChatUser getOwner() {
		return owner;
	}

	public <T extends ChatCreate> T setOwner(ChatUser owner) {
		this.owner = owner;
		return (T) this;
	}
}
