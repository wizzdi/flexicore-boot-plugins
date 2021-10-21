package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.GatewayCreate;
import com.wizzdi.basic.iot.service.request.GatewayFilter;
import com.wizzdi.basic.iot.service.request.GatewayUpdate;
import com.wizzdi.basic.iot.model.Gateway;
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

public class GatewayControllerTest {

    private Gateway gateway;
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
    public void testGatewayCreate() {
        String name = UUID.randomUUID().toString();
        GatewayCreate request = new GatewayCreate()
                .setName(name);
        ResponseEntity<Gateway> gatewayResponse = this.restTemplate.postForEntity("/plugins/Gateway/createGateway", request, Gateway.class);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        gateway = gatewayResponse.getBody();
        assertGateway(request, gateway);

    }

    @Test
    @Order(2)
    public void testGetAllGateways() {
        GatewayFilter request=new GatewayFilter();
        ParameterizedTypeReference<PaginationResponse<Gateway>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Gateway>> gatewayResponse = this.restTemplate.exchange("/plugins/Gateway/getAllGateways", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        PaginationResponse<Gateway> body = gatewayResponse.getBody();
        Assertions.assertNotNull(body);
        List<Gateway> gateways = body.getList();
        Assertions.assertNotEquals(0,gateways.size());
        Assertions.assertTrue(gateways.stream().anyMatch(f->f.getId().equals(gateway.getId())));


    }

    public void assertGateway(GatewayCreate request, Gateway gateway) {
        Assertions.assertNotNull(gateway);
        Assertions.assertEquals(request.getName(), gateway.getName());
    }

    @Test
    @Order(3)
    public void testGatewayUpdate(){
        String name = UUID.randomUUID().toString();
        GatewayUpdate request = new GatewayUpdate()
                .setId(gateway.getId())
                .setName(name);
        ResponseEntity<Gateway> gatewayResponse = this.restTemplate.exchange("/plugins/Gateway/updateGateway",HttpMethod.PUT, new HttpEntity<>(request), Gateway.class);
        Assertions.assertEquals(200, gatewayResponse.getStatusCodeValue());
        gateway = gatewayResponse.getBody();
        assertGateway(request, gateway);

    }

}
