package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.client.BasicIOTClient;
import com.wizzdi.basic.iot.model.ConnectivityChange;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeCreate;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeFilter;
import com.wizzdi.basic.iot.service.request.ConnectivityChangeUpdate;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.apache.commons.io.FileUtils;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.util.encoders.Base64;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.shaded.org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = App.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // deactivate the default behaviour

public class ConnectivityChangeControllerTest {

    private final static PostgreSQLContainer postgresqlContainer = new PostgreSQLContainer("postgres:15")

            .withDatabaseName("flexicore-test")
            .withUsername("flexicore")
            .withPassword("flexicore");
    private final static HiveMQContainer hivemqCe = new HiveMQContainer(DockerImageName.parse("hivemq/hivemq-ce").withTag("2023.5"));



    static {
        postgresqlContainer.start();
        hivemqCe.start();

    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);

        registry.add("basic.iot.mqtt.url", ()->"tcp://"+hivemqCe.getHost()+":"+hivemqCe.getMqttPort());
        KeyPairForTests.KeyPair keyPair = KeyPairForTests.getKeyPair();
        File privateKey = keyPair.privateKey();
        registry.add("basic.iot.keyPath", privateKey::getAbsolutePath);

    }

    private ConnectivityChange connectivityChange;
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
    public void testConnectivityChangeCreate() {
        String name = UUID.randomUUID().toString();
        ConnectivityChangeCreate request = new ConnectivityChangeCreate()
                .setName(name);
        ResponseEntity<ConnectivityChange> connectivityChangeResponse = this.restTemplate.postForEntity("/plugins/ConnectivityChange/createConnectivityChange", request, ConnectivityChange.class);
        Assertions.assertEquals(200, connectivityChangeResponse.getStatusCodeValue());
        connectivityChange = connectivityChangeResponse.getBody();
        assertConnectivityChange(request, connectivityChange);

    }

    @Test
    @Order(2)
    public void testGetAllConnectivityChanges() {
        ConnectivityChangeFilter request=new ConnectivityChangeFilter();
        ParameterizedTypeReference<PaginationResponse<ConnectivityChange>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<ConnectivityChange>> connectivityChangeResponse = this.restTemplate.exchange("/plugins/ConnectivityChange/getAllConnectivityChanges", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, connectivityChangeResponse.getStatusCodeValue());
        PaginationResponse<ConnectivityChange> body = connectivityChangeResponse.getBody();
        Assertions.assertNotNull(body);
        List<ConnectivityChange> connectivityChanges = body.getList();
        Assertions.assertNotEquals(0,connectivityChanges.size());
        Assertions.assertTrue(connectivityChanges.stream().anyMatch(f->f.getId().equals(connectivityChange.getId())));


    }

    public void assertConnectivityChange(ConnectivityChangeCreate request, ConnectivityChange connectivityChange) {
        Assertions.assertNotNull(connectivityChange);
        Assertions.assertEquals(request.getName(), connectivityChange.getName());
    }

    @Test
    @Order(3)
    public void testConnectivityChangeUpdate(){
        String name = UUID.randomUUID().toString();
        ConnectivityChangeUpdate request = new ConnectivityChangeUpdate()
                .setId(connectivityChange.getId())
                .setName(name);
        ResponseEntity<ConnectivityChange> connectivityChangeResponse = this.restTemplate.exchange("/plugins/ConnectivityChange/updateConnectivityChange",HttpMethod.PUT, new HttpEntity<>(request), ConnectivityChange.class);
        Assertions.assertEquals(200, connectivityChangeResponse.getStatusCodeValue());
        connectivityChange = connectivityChangeResponse.getBody();
        assertConnectivityChange(request, connectivityChange);

    }

}
