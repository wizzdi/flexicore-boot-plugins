package com.wizzdi.messaging.firebase;


import com.flexicore.security.SecurityContextBase;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import com.wizzdi.messaging.connectors.firebase.model.FirebaseEnabledDevice;
import com.wizzdi.messaging.firebase.app.App;
import com.wizzdi.messaging.firebase.app.SecurityInterceptor;
import com.wizzdi.messaging.firebase.app.SecurityServiceTest;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceCreate;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceFilter;
import com.wizzdi.messaging.firebase.request.FirebaseEnabledDeviceUpdate;
import com.wizzdi.messaging.model.ChatUser;
import com.wizzdi.messaging.request.ChatUserCreate;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {App.class})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
public class FirebaseEnabledDeviceControllerTest {

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


    private ChatUser chatUser;
    private FirebaseEnabledDevice firebaseEnabledDevice;


    @BeforeAll
    private void init() {
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
    public void createFirebaseEnabledDevice() throws InterruptedException {
        FirebaseEnabledDeviceCreate request = new FirebaseEnabledDeviceCreate()
                .setExternalId("test")
                .setName("test firebaseEnabledDevice");

        ParameterizedTypeReference<FirebaseEnabledDevice> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<FirebaseEnabledDevice> response = this.restTemplate.exchange("/firebaseEnabledDevice/createFirebaseEnabledDevice", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        firebaseEnabledDevice = response.getBody();
        Assertions.assertNotNull(firebaseEnabledDevice);
        Assertions.assertEquals(request.getName(),firebaseEnabledDevice.getName());
        Assertions.assertEquals(request.getExternalId(),firebaseEnabledDevice.getExternalId());
        Assertions.assertEquals(chatUser.getId(),firebaseEnabledDevice.getOwner().getId());


    }

    @Test
    @Order(2)
    public void testGetAllFirebaseEnabledDevices() throws InterruptedException {
        FirebaseEnabledDeviceFilter request = new FirebaseEnabledDeviceFilter();
        ParameterizedTypeReference<PaginationResponse<FirebaseEnabledDevice>> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<PaginationResponse<FirebaseEnabledDevice>> response = this.restTemplate.exchange("/firebaseEnabledDevice/getAllFirebaseEnabledDevices", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        PaginationResponse<FirebaseEnabledDevice> body = response.getBody();
        Assertions.assertNotNull(body);
        List<FirebaseEnabledDevice> firebaseEnabledDevices = body.getList();
        Assertions.assertTrue(firebaseEnabledDevices.stream().anyMatch(f->f.getId().equals(firebaseEnabledDevice.getId())));

    }

    @Test
    @Order(2)
    public void updateFirebaseEnabledDevice() throws InterruptedException {
        FirebaseEnabledDeviceUpdate request = new FirebaseEnabledDeviceUpdate()
                .setId(firebaseEnabledDevice.getId())
                .setExternalId("test new ")
                .setName("test firebaseEnabledDevice new");

        ParameterizedTypeReference<FirebaseEnabledDevice> t = new ParameterizedTypeReference<>() {};
        ResponseEntity<FirebaseEnabledDevice> response = this.restTemplate.exchange("/firebaseEnabledDevice/updateFirebaseEnabledDevice", HttpMethod.PUT, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        firebaseEnabledDevice = response.getBody();
        Assertions.assertNotNull(firebaseEnabledDevice);
        Assertions.assertEquals(request.getName(),firebaseEnabledDevice.getName());
        Assertions.assertEquals(request.getExternalId(),firebaseEnabledDevice.getExternalId());
        Assertions.assertEquals(chatUser.getId(),firebaseEnabledDevice.getOwner().getId());


    }


}
