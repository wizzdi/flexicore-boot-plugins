package com.wizzdi.messaging.events;

import com.wizzdi.messaging.model.Message;

public class DeletedMessageEvent {

	private final Message message;

	public DeletedMessageEvent(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
