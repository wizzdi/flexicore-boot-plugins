package com.wizzdi.messaging.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wizzdi.messaging.model.Message;

public class MessageUpdate extends MessageCreate {

	private String id;
	@JsonIgnore
	private Message message;

	public String getId() {
		return id;
	}

	public <T extends MessageUpdate> T setId(String id) {
		this.id = id;
		return (T) this;
	}

	@JsonIgnore
	public Message getMessage() {
		return message;
	}

	public <T extends MessageUpdate> T setMessage(Message message) {
		this.message = message;
		return (T) this;
	}
}
