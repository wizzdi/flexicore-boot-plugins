package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.model.DeviceType;
import com.wizzdi.basic.iot.service.triggers.app.App;
import com.wizzdi.basic.iot.service.request.DeviceTypeCreate;
import com.wizzdi.basic.iot.service.request.DeviceTypeFilter;
import com.wizzdi.basic.iot.service.request.DeviceTypeUpdate;
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
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.utility.DockerImageName;

import java.io.File;
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

public class DeviceTypeControllerTest {
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

    private DeviceType deviceType;
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
    public void testDeviceTypeCreate() {
        String name = UUID.randomUUID().toString();
        DeviceTypeCreate request = new DeviceTypeCreate()
                .setName(name);
        ResponseEntity<DeviceType> deviceTypeResponse = this.restTemplate.postForEntity("/plugins/DeviceType/createDeviceType", request, DeviceType.class);
        Assertions.assertEquals(200, deviceTypeResponse.getStatusCodeValue());
        deviceType = deviceTypeResponse.getBody();
        assertDeviceType(request, deviceType);

    }

    @Test
    @Order(2)
    public void testGetAllDeviceTypes() {
        DeviceTypeFilter request=new DeviceTypeFilter();
        ParameterizedTypeReference<PaginationResponse<DeviceType>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<DeviceType>> deviceTypeResponse = this.restTemplate.exchange("/plugins/DeviceType/getAllDeviceTypes", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, deviceTypeResponse.getStatusCodeValue());
        PaginationResponse<DeviceType> body = deviceTypeResponse.getBody();
        Assertions.assertNotNull(body);
        List<DeviceType> deviceTypes = body.getList();
        Assertions.assertNotEquals(0,deviceTypes.size());
        Assertions.assertTrue(deviceTypes.stream().anyMatch(f->f.getId().equals(deviceType.getId())));


    }

    public void assertDeviceType(DeviceTypeCreate request, DeviceType deviceType) {
        Assertions.assertNotNull(deviceType);
        Assertions.assertEquals(request.getName(), deviceType.getName());
    }

    @Test
    @Order(3)
    public void testDeviceTypeUpdate(){
        String name = UUID.randomUUID().toString();
        DeviceTypeUpdate request = new DeviceTypeUpdate()
                .setId(deviceType.getId())
                .setName(name);
        ResponseEntity<DeviceType> deviceTypeResponse = this.restTemplate.exchange("/plugins/DeviceType/updateDeviceType",HttpMethod.PUT, new HttpEntity<>(request), DeviceType.class);
        Assertions.assertEquals(200, deviceTypeResponse.getStatusCodeValue());
        deviceType = deviceTypeResponse.getBody();
        assertDeviceType(request, deviceType);

    }

}
