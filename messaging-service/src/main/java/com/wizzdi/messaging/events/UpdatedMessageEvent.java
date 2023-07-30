package com.wizzdi.messaging.events;

import com.wizzdi.messaging.model.Message;

public class UpdatedMessageEvent {

	private final Message message;

	public UpdatedMessageEvent(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
