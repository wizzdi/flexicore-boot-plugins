package com.wizzdi.messaging;


import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.app.App;
import com.wizzdi.messaging.app.SecurityInterceptor;
import com.wizzdi.messaging.app.SecurityServiceTest;
import com.wizzdi.messaging.model.Chat;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.model.Message;
import com.wizzdi.messaging.request.ChatUserCreate;
import com.wizzdi.messaging.request.MessageCreate;
import com.wizzdi.messaging.request.MessageFilter;
import com.wizzdi.messaging.request.MessageUpdate;
import com.wizzdi.messaging.service.ChatUserService;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {App.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour
public class MessageControllerTest {

    private final static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")

            .withDatabaseName("flexicore-test")
            .withUsername("flexicore")
            .withPassword("flexicore");

    static {
        postgresqlContainer.start();
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private ChatUserService chatUserService;
    @Autowired
    @Lazy
    @Qualifier("adminSecurityContext")
    private SecurityContextBase securityContextBase;
    @Autowired
    private SecurityServiceTest securityServiceTest;
    @Autowired
    private Chat chat;


    private ChatUser chatUser;
    private Message message;


    @BeforeAll
    public void init() {
        AtomicReference<String> reference=new AtomicReference<>("admin");
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", reference.get());
                    return execution.execute(request, body);
                }));
        chatUser = chatUserService.createChatUser(new ChatUserCreate(), securityContextBase);
        SecurityInterceptor.setChatUser(chatUser);
    }

    @Test
    @Order(1)
    public void createMessage() throws InterruptedException {
        MessageCreate request = new MessageCreate()
                .setChatId(chat.getId())
                .setContent("test")
                .setName("test message");

        ParameterizedTypeReference<Message> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Message> response = this.restTemplate.exchange("/message/createMessage", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertEquals(request.getName(),message.getName());
        Assertions.assertEquals(request.getContent(),message.getContent());
        Assertions.assertEquals(chatUser.getId(),message.getSender().getId());


    }

    @Test
    @Order(2)
    public void testGetAllMessages() throws InterruptedException {
        MessageFilter request = new MessageFilter();
        request.setChatsIds(Collections.singleton(chat.getId()));
        ParameterizedTypeReference<PaginationResponse<Message>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<Message>> response = this.restTemplate.exchange("/message/getAllMessages", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        PaginationResponse<Message> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Message> messages = body.getList();
        Assertions.assertTrue(messages.stream().anyMatch(f->f.getId().equals(message.getId())));

    }
    @Test
    @Order(3)
    public void testGetUnread() throws InterruptedException {
        MessageFilter request = new MessageFilter();
        request.setChatsIds(Collections.singleton(chat.getId()));
        request.setUnreadByIds(Collections.singleton(chatUser.getId()));
        ParameterizedTypeReference<PaginationResponse<Message>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<Message>> response = this.restTemplate.exchange("/message/getAllMessages", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        PaginationResponse<Message> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Message> messages = body.getList();
        Assertions.assertTrue(messages.stream().anyMatch(f->f.getId().equals(message.getId())));

    }

    @Test
    @Order(4)
    public void testMarkRead() throws InterruptedException {
        Map<String, OffsetDateTime> readMap=new HashMap<>();
        readMap.put(chatUser.getId(),OffsetDateTime.now());
        MessageUpdate request = new MessageUpdate()
                .setId(message.getId())
                .setChatUsers(readMap);

        ParameterizedTypeReference<Message> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<Message> response = this.restTemplate.exchange("/message/updateMessage", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        message = response.getBody();
        Assertions.assertNotNull(message);
        Assertions.assertTrue(request.getChatUsers().containsKey(chatUser.getId()));

    }

    @Test
    @Order(5)
    public void testGetUnreadShouldBeEmpty() throws InterruptedException {
        MessageFilter request = new MessageFilter();
        request.setChatsIds(Collections.singleton(chat.getId()));
        request.setUnreadByIds(Collections.singleton(chatUser.getId()));
        ParameterizedTypeReference<PaginationResponse<Message>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<Message>> response = this.restTemplate.exchange("/message/getAllMessages", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful() );
        PaginationResponse<Message> body = response.getBody();
        Assertions.assertNotNull(body);
        List<Message> messages = body.getList();
        Assertions.assertTrue(messages.stream().noneMatch(f->f.getId().equals(message.getId())));

    }


}
