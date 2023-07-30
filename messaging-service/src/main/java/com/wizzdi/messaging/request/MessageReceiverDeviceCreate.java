package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.flexicore.security.request.BasicCreate;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.MessageReceiverDevice;

public class MessageReceiverDeviceCreate extends BasicCreate {

	private String ownerId;
	@JsonIgnore
	private ChatUser owner;
	private String externalId;

	public String getOwnerId() {
		return ownerId;
	}

	public <T extends MessageReceiverDeviceCreate> T setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		return (T) this;
	}

	@JsonIgnore
	public ChatUser getOwner() {
		return owner;
	}

	public <T extends MessageReceiverDeviceCreate> T setOwner(ChatUser owner) {
		this.owner = owner;
		return (T) this;
	}

	public String getExternalId() {
		return externalId;
	}

	public <T extends MessageReceiverDeviceCreate> T setExternalId(String externalId) {
		this.externalId = externalId;
		return (T) this;
	}
}
