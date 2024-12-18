package com.wizzdi.messaging.interfaces;

import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.messaging.model.ChatUser;

public interface ChatUserProvider<T extends SecurityUser> {

	ChatUser getChatUser(SecurityContext SecurityContext);
	Class<T> getType();

}
