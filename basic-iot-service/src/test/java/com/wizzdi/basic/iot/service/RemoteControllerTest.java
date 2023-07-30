package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.model.Remote;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.RemoteCreate;
import com.wizzdi.basic.iot.service.request.RemoteFilter;
import com.wizzdi.basic.iot.service.request.RemoteUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")

public class RemoteControllerTest {

    private Remote remote;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    private void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void testRemoteCreate() {
        String name = UUID.randomUUID().toString();
        RemoteCreate request = new RemoteCreate()
                .setName(name);
        ResponseEntity<Remote> remoteResponse = this.restTemplate.postForEntity("/plugins/Remote/createRemote", request, Remote.class);
        Assertions.assertEquals(200, remoteResponse.getStatusCodeValue());
        remote = remoteResponse.getBody();
        assertRemote(request, remote);

    }

    @Test
    @Order(2)
    public void testGetAllRemotes() {
        RemoteFilter request=new RemoteFilter();
        ParameterizedTypeReference<PaginationResponse<Remote>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Remote>> remoteResponse = this.restTemplate.exchange("/plugins/Remote/getAllRemotes", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, remoteResponse.getStatusCodeValue());
        PaginationResponse<Remote> body = remoteResponse.getBody();
        Assertions.assertNotNull(body);
        List<Remote> remotes = body.getList();
        Assertions.assertNotEquals(0,remotes.size());
        Assertions.assertTrue(remotes.stream().anyMatch(f->f.getId().equals(remote.getId())));


    }

    public void assertRemote(RemoteCreate request, Remote remote) {
        Assertions.assertNotNull(remote);
        Assertions.assertEquals(request.getName(), remote.getName());
    }

    @Test
    @Order(3)
    public void testRemoteUpdate(){
        String name = UUID.randomUUID().toString();
        RemoteUpdate request = new RemoteUpdate()
                .setId(remote.getId())
                .setName(name);
        ResponseEntity<Remote> remoteResponse = this.restTemplate.exchange("/plugins/Remote/updateRemote",HttpMethod.PUT, new HttpEntity<>(request), Remote.class);
        Assertions.assertEquals(200, remoteResponse.getStatusCodeValue());
        remote = remoteResponse.getBody();
        assertRemote(request, remote);

    }

}
