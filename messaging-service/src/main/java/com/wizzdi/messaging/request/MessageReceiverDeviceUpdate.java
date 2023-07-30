package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.messaging.model.MessageReceiverDevice;

public class MessageReceiverDeviceUpdate extends MessageReceiverDeviceCreate {

	private String id;
	@JsonIgnore
	private MessageReceiverDevice messageReceiverDevice;

	public String getId() {
		return id;
	}

	public <T extends MessageReceiverDeviceUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public MessageReceiverDevice getMessageReceiverDevice() {
		return messageReceiverDevice;
	}

	public <T extends MessageReceiverDeviceUpdate> T setMessageReceiverDevice(MessageReceiverDevice messageReceiverDevice) {
		this.messageReceiverDevice = messageReceiverDevice;
		return (T) this;
	}
}
