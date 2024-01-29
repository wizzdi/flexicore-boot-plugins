package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.model.Device;
import com.wizzdi.basic.iot.service.app.App;
import com.wizzdi.basic.iot.service.request.DeviceCreate;
import com.wizzdi.basic.iot.service.request.DeviceFilter;
import com.wizzdi.basic.iot.service.request.DeviceUpdate;
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
import org.springframework.test.annotation.DirtiesContext;
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
@DirtiesContext
public class DeviceControllerTest {

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

    private Device device;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public void init() {
        restTemplate.getRestTemplate().setInterceptors(
                Collections.singletonList((request, body, execution) -> {
                    request.getHeaders()
                            .add("authenticationKey", "fake");
                    return execution.execute(request, body);
                }));

    }

    @Test
    @Order(1)
    public void testDeviceCreate() {
        String name = UUID.randomUUID().toString();
        DeviceCreate request = new DeviceCreate()
                .setName(name);
        ResponseEntity<Device> deviceResponse = this.restTemplate.postForEntity("/plugins/Device/createDevice", request, Device.class);
        Assertions.assertEquals(200, deviceResponse.getStatusCodeValue());
        device = deviceResponse.getBody();
        assertDevice(request, device);

    }

    @Test
    @Order(2)
    public void testGetAllDevices() {
        DeviceFilter request=new DeviceFilter();
        ParameterizedTypeReference<PaginationResponse<Device>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<Device>> deviceResponse = this.restTemplate.exchange("/plugins/Device/getAllDevices", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, deviceResponse.getStatusCodeValue());
        PaginationResponse<Device> body = deviceResponse.getBody();
        Assertions.assertNotNull(body);
        List<Device> devices = body.getList();
        Assertions.assertNotEquals(0,devices.size());
        Assertions.assertTrue(devices.stream().anyMatch(f->f.getId().equals(device.getId())));


    }

    public void assertDevice(DeviceCreate request, Device device) {
        Assertions.assertNotNull(device);
        Assertions.assertEquals(request.getName(), device.getName());
    }

    @Test
    @Order(3)
    public void testDeviceUpdate(){
        String name = UUID.randomUUID().toString();
        DeviceUpdate request = new DeviceUpdate()
                .setId(device.getId())
                .setName(name);
        ResponseEntity<Device> deviceResponse = this.restTemplate.exchange("/plugins/Device/updateDevice",HttpMethod.PUT, new HttpEntity<>(request), Device.class);
        Assertions.assertEquals(200, deviceResponse.getStatusCodeValue());
        device = deviceResponse.getBody();
        assertDevice(request, device);

    }

}
