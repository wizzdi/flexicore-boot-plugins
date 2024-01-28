package com.wizzdi.basic.iot.service;

import com.wizzdi.basic.iot.model.FirmwareUpdate;
import com.wizzdi.basic.iot.service.triggers.app.App;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateCreate;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateFilter;
import com.wizzdi.basic.iot.service.request.FirmwareUpdateUpdate;
import com.wizzdi.flexicore.file.model.FileResource;
import com.wizzdi.flexicore.security.response.PaginationResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Lazy;
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

public class FirmwareUpdateControllerTest {
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

    private FirmwareUpdate firmwareUpdate;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    @Lazy
    private ObjectHolder<FileResource> firmwareFile;

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
    public void testFirmwareUpdateCreate() {
        String name = UUID.randomUUID().toString();
        FirmwareUpdateCreate request = new FirmwareUpdateCreate()
                .setVersion("2.0.0")
                .setFileResourceId(firmwareFile.get().getId())
                .setName(name);
        ResponseEntity<FirmwareUpdate> firmwareUpdateResponse = this.restTemplate.postForEntity("/plugins/FirmwareUpdate/createFirmwareUpdate", request, FirmwareUpdate.class);
        Assertions.assertEquals(200, firmwareUpdateResponse.getStatusCodeValue(),()->"status: "+firmwareUpdateResponse.getStatusCode());
        firmwareUpdate = firmwareUpdateResponse.getBody();
        assertFirmwareUpdate(request, firmwareUpdate);

    }

    @Test
    @Order(2)
    public void testGetAllFirmwareUpdates() {
        FirmwareUpdateFilter request=new FirmwareUpdateFilter();
        ParameterizedTypeReference<PaginationResponse<FirmwareUpdate>> t= new ParameterizedTypeReference<>() {
        };

        ResponseEntity<PaginationResponse<FirmwareUpdate>> firmwareUpdateResponse = this.restTemplate.exchange("/plugins/FirmwareUpdate/getAllFirmwareUpdates", HttpMethod.POST, new HttpEntity<>(request), t);
        Assertions.assertEquals(200, firmwareUpdateResponse.getStatusCodeValue());
        PaginationResponse<FirmwareUpdate> body = firmwareUpdateResponse.getBody();
        Assertions.assertNotNull(body);
        List<FirmwareUpdate> firmwareUpdates = body.getList();
        Assertions.assertNotEquals(0,firmwareUpdates.size());
        Assertions.assertTrue(firmwareUpdates.stream().anyMatch(f->f.getId().equals(firmwareUpdate.getId())));


    }

    public void assertFirmwareUpdate(FirmwareUpdateCreate request, FirmwareUpdate firmwareUpdate) {
        Assertions.assertNotNull(firmwareUpdate);
        Assertions.assertEquals(request.getName(), firmwareUpdate.getName());
    }

    @Test
    @Order(3)
    public void testFirmwareUpdateUpdate(){
        String name = UUID.randomUUID().toString();
        FirmwareUpdateUpdate request = new FirmwareUpdateUpdate()
                .setId(firmwareUpdate.getId())
                .setName(name);
        ResponseEntity<FirmwareUpdate> firmwareUpdateResponse = this.restTemplate.exchange("/plugins/FirmwareUpdate/updateFirmwareUpdate",HttpMethod.PUT, new HttpEntity<>(request), FirmwareUpdate.class);
        Assertions.assertEquals(200, firmwareUpdateResponse.getStatusCodeValue());
        firmwareUpdate = firmwareUpdateResponse.getBody();
        assertFirmwareUpdate(request, firmwareUpdate);

    }

}
