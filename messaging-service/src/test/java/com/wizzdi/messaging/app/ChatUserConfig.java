package com.wizzdi.messaging.app;

import com.flexicore.model.SecurityUser;
import com.flexicore.security.SecurityContextBase;
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
			public ChatUser getChatUser(SecurityContextBase<?, SecurityUser, ?, ?> securityContextBase) {
				if (securityContextBase instanceof ChatUserSecurityContext) {
					return ((ChatUserSecurityContext) securityContextBase).getChatUser();
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
