package com.wizzdi.messaging.app;

import com.flexicore.security.SecurityContextBase;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.request.ChatCreate;
import com.wizzdi.messaging.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestEntities {

    @Autowired
    private ChatService chatService;
    @Autowired
    private SecurityContextBase adminSecurityContext;
    @Bean
    public Chat chat(){
        return chatService.createChat(new ChatCreate().setName("test"),adminSecurityContext);
    }
}
