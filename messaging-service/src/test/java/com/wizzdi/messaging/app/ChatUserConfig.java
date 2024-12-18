package com.wizzdi.messaging.app;

import com.flexicore.model.SecurityUser;
import com.wizzdi.flexicore.security.configuration.SecurityContext;
import com.wizzdi.messaging.interfaces.ChatUserProvider;
import com.wizzdi.messaging.model.ChatUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatUserConfig {

	@Bean
	public ChatUserProvider<SecurityUser> chatUserProvider(){
		return new ChatUserProvider<>() {
			@Override
			public ChatUser getChatUser(SecurityContext SecurityContext) {
				if (SecurityContext instanceof ChatUserSecurityContext) {
					return ((ChatUserSecurityContext) SecurityContext).getChatUser();
				}
				return null;
			}

			@Override
			public Class<SecurityUser> getType() {
				return SecurityUser.class;
			}
		};
	}
}
