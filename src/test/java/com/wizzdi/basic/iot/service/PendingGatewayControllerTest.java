package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.model.PendingGateway;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.PendingGatewayCreate;
import com.wizzdi.basic.iot.service.request.PendingGatewayFilter;
import com.wizzdi.basic.iot.service.request.PendingGatewayUpdate;
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

public class PendingGatewayControllerTest {

    private PendingGateway pendingGateway;
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
    public void testPendingGatewayCreate() {
        String name = UUID.randomUUID().toString();
        PendingGatewayCreate request = new PendingGatewayCreate()
                .setName(name);
        ResponseEntity<PendingGateway> pendingGatewayResponse = this.restTemplate.postForEntity("/plugins/PendingGateway/createPendingGateway", request, PendingGateway.class);
        Assertions.assertEquals(200, pendingGatewayResponse.getStatusCodeValue());
        pendingGateway = pendingGatewayResponse.getBody();
        assertPendingGateway(request, pendingGateway);

    }

    @Test
    @Order(2)
    public void testGetAllPendingGateways() {
        PendingGatewayFilter request=new PendingGatewayFilter();
        ParameterizedTypeReference<PaginationResponse<PendingGateway>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<PendingGateway>> pendingGatewayResponse = this.restTemplate.exchange("/plugins/PendingGateway/getAllPendingGateways", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, pendingGatewayResponse.getStatusCodeValue());
        PaginationResponse<PendingGateway> body = pendingGatewayResponse.getBody();
        Assertions.assertNotNull(body);
        List<PendingGateway> pendingGateways = body.getList();
        Assertions.assertNotEquals(0,pendingGateways.size());
        Assertions.assertTrue(pendingGateways.stream().anyMatch(f->f.getId().equals(pendingGateway.getId())));


    }

    public void assertPendingGateway(PendingGatewayCreate request, PendingGateway pendingGateway) {
        Assertions.assertNotNull(pendingGateway);
        Assertions.assertEquals(request.getName(), pendingGateway.getName());
    }

    @Test
    @Order(3)
    public void testPendingGatewayUpdate(){
        String name = UUID.randomUUID().toString();
        PendingGatewayUpdate request = new PendingGatewayUpdate()
                .setId(pendingGateway.getId())
                .setName(name);
        ResponseEntity<PendingGateway> pendingGatewayResponse = this.restTemplate.exchange("/plugins/PendingGateway/updatePendingGateway",HttpMethod.PUT, new HttpEntity<>(request), PendingGateway.class);
        Assertions.assertEquals(200, pendingGatewayResponse.getStatusCodeValue());
        pendingGateway = pendingGatewayResponse.getBody();
        assertPendingGateway(request, pendingGateway);

    }

}
