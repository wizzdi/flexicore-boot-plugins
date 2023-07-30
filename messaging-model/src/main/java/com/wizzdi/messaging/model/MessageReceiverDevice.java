package com.wizzdi.messaging.model;

import com.flexicore.model.SecuredBasic;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

@Entity
public class MessageReceiverDevice extends SecuredBasic {

	@ManyToOne(targetEntity = ChatUser.class)
	private ChatUser owner;
	private String externalId;

	@ManyToOne(targetEntity = ChatUser.class)
	public ChatUser getOwner() {
		return owner;
	}

	public <T extends MessageReceiverDevice> T setOwner(ChatUser chatUser) {
		this.owner = chatUser;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends MessageReceiverDevice> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}
}
