package com.wizzdi.messaging;


import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.app.App;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.request.ChatUserCreate;
import com.wizzdi.messaging.request.ChatUserFilter;
import com.wizzdi.messaging.request.ChatUserUpdate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {App.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class ChatUserControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    @Lazy
    @Qualifier("adminSecurityContext")
    private SecurityContextBase securityContextBase;



    private ChatUser chatUser;


    @BeforeAll
    private void init() {
        AtomicReference<String> reference=new AtomicReference<>("admin");
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", reference.get());
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void createChatUser() throws InterruptedException {
        ChatUserCreate request = new ChatUserCreate()
                .setName("test chatUser");

        ParameterizedTypeReference<ChatUser> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<ChatUser> response = this.restTemplate.exchange("/chatUser/createChatUser", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        chatUser = response.getBody();
        Assertions.assertNotNull(chatUser);
        Assertions.assertEquals(request.getName(),chatUser.getName());


    }

    @Test
    @Order(2)
    public void testGetAllChatUsers() throws InterruptedException {
        ChatUserFilter request = new ChatUserFilter();
        ParameterizedTypeReference<PaginationResponse<ChatUser>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<ChatUser>> response = this.restTemplate.exchange("/chatUser/getAllChatUsers", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<ChatUser> body = response.getBody();
        Assertions.assertNotNull(body);
        List<ChatUser> chatUsers = body.getList();
        Assertions.assertTrue(chatUsers.stream().anyMatch(f->f.getId().equals(chatUser.getId())));

    }

    @Test
    @Order(3)
    public void testUpdateChatUser() throws InterruptedException {
        ChatUserUpdate request = new ChatUserUpdate()
                .setId(chatUser.getId())
                .setName("new name");
        ParameterizedTypeReference<ChatUser> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<ChatUser> response = this.restTemplate.exchange("/chatUser/updateChatUser", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        chatUser= response.getBody();
        Assertions.assertNotNull(chatUser);
        Assertions.assertEquals(request.getName(),chatUser.getName());

    }


}
