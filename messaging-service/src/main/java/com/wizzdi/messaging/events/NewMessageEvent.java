package com.wizzdi.messaging.events;

import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.messaging.model.Message;

public class NewMessageEvent {

	private final Message message;
	private final SecurityContext SecurityContext;

	public NewMessageEvent(Message message,SecurityContext SecurityContext) {
		this.message = message;
		this.SecurityContext=SecurityContext;
	}

	public Message getMessage() {
		return message;
	}

	public SecurityContext getSecurityContext() {
		return SecurityContext;
	}
}
